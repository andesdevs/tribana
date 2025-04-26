<?php
/**
 * api_perfil.php
 *
 * Este script actúa como API para gestionar el perfil de usuarios.
 * Se conecta directamente a la base de datos usando las credenciales definidas en db_config.php.
 *
 * Acciones:
 *   - obtener_perfil (GET): Recupera el perfil basado en el parámetro "email".
 *   - guardar_perfil (POST): Inserta o actualiza el perfil enviado en formato JSON.
 *
 * Para proteger el acceso, se requiere que la app envíe los parámetros "app_user" y "app_pass"
 * que se comparan con los valores definidos en db_config.php (API_USER y API_PASS).
 */

// Incluir configuración de la base de datos y credenciales de API
require_once('db_config.php');

// Establecer la cabecera de respuesta como JSON
header('Content-Type: application/json');

// --- AUTENTICACIÓN SIMPLE ---
// Se espera que la app envíe "app_user" y "app_pass" en GET o POST
$app_user = $_GET['app_user'] ?? $_POST['app_user'] ?? '';
$app_pass = $_GET['app_pass'] ?? $_POST['app_pass'] ?? '';

if ($app_user !== API_USER || $app_pass !== API_PASS) {
    http_response_code(403);
    echo json_encode(['error' => 'Acceso denegado: credenciales incorrectas']);
    exit;
}

// --- CONEXIÓN A LA BASE DE DATOS ---
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(['error' => 'Error de conexión: ' . $conn->connect_error]);
    exit;
}

// Función auxiliar para enviar respuestas JSON y terminar la ejecución.
function sendResponse($data, $code = 200) {
    http_response_code($code);
    echo json_encode($data);
    exit;
}

// Detectar la acción (se espera un parámetro "action" en GET o POST)
$action = $_GET['action'] ?? $_POST['action'] ?? '';

// Nombre de la tabla donde se almacenan los perfiles
$table = "wp_perfil_usuarios";

switch ($action) {

    case 'obtener_perfil':
        // Se espera un parámetro "email"
        $email = $_GET['email'] ?? '';
        if (empty($email)) {
            sendResponse(['error' => 'Parámetro email requerido'], 400);
        }
        $email = $conn->real_escape_string($email);
        $sql = "SELECT * FROM $table WHERE user_email = '$email'";
        $result = $conn->query($sql);
        if ($result && $result->num_rows > 0) {
            $perfil = $result->fetch_assoc();
            sendResponse($perfil);
        } else {
            sendResponse(['error' => 'Perfil no encontrado'], 404);
        }
        break;

    case 'guardar_perfil':
        // Se espera recibir datos en formato JSON en el cuerpo de la solicitud
        $data = json_decode(file_get_contents('php://input'), true);
        if (!$data) {
            sendResponse(['error' => 'JSON inválido'], 400);
        }
        // Campos obligatorios: user_email, rut, nombre
        if (empty($data['user_email']) || empty($data['rut']) || empty($data['nombre'])) {
            sendResponse(['error' => 'Faltan campos obligatorios (user_email, rut, nombre)'], 400);
        }
        $user_email = $conn->real_escape_string($data['user_email']);
        $rut        = $conn->real_escape_string($data['rut']);
        $nombre     = $conn->real_escape_string($data['nombre']);
        $direccion  = isset($data['direccion']) ? $conn->real_escape_string($data['direccion']) : '';
        $telefono   = isset($data['telefono']) ? $conn->real_escape_string($data['telefono']) : '';
        // Si "etiquetas" es un array, conviértelo a JSON; si es cadena, se guarda tal cual.
        $etiquetas  = isset($data['etiquetas']) ? $conn->real_escape_string(json_encode($data['etiquetas'])) : '';

        // Verificar si ya existe un perfil para ese email
        $checkSql = "SELECT * FROM $table WHERE user_email = '$user_email'";
        $checkResult = $conn->query($checkSql);
        if ($checkResult && $checkResult->num_rows > 0) {
            // Actualizar perfil
            $updateSql = "UPDATE $table SET rut='$rut', nombre='$nombre', direccion='$direccion', telefono='$telefono', etiquetas='$etiquetas' WHERE user_email='$user_email'";
            if ($conn->query($updateSql)) {
                sendResponse(['status' => 'actualizado']);
            } else {
                sendResponse(['error' => 'Error al actualizar: ' . $conn->error], 500);
            }
        } else {
            // Insertar nuevo perfil
            $insertSql = "INSERT INTO $table (user_email, rut, nombre, direccion, telefono, etiquetas, fecha_registro) VALUES ('$user_email', '$rut', '$nombre', '$direccion', '$telefono', '$etiquetas', NOW())";
            if ($conn->query($insertSql)) {
                sendResponse(['status' => 'insertado']);
            } else {
                sendResponse(['error' => 'Error al insertar: ' . $conn->error], 500);
            }
        }
        break;

    default:
        sendResponse(['error' => 'Acción no especificada o inválida'], 400);
        break;
}

$conn->close();
