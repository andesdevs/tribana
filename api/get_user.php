<?php
include 'db_config.php';

header("Content-Type: application/json");

// Habilitar depuración en desarrollo
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Conectar a la base de datos
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    error_log("Error de conexión: " . $conn->connect_error);
    die(json_encode(["error" => "Error en la conexión a la base de datos"]));
}

// Verificar si se recibió el parámetro rut
if (!isset($_GET['rut'])) {
    die(json_encode(["error" => "Falta el parámetro rut"]));
}

$rut_postulante = $_GET['rut'];
error_log("Consultando información del RUT: $rut_postulante");

// 📌 Obtener la información del usuario
$sql = "SELECT nombre, foto_perfil FROM wp_perfil_usuarios WHERE rut = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $rut_postulante);
$stmt->execute();
$result = $stmt->get_result();
$usuario = $result->fetch_assoc();

// 📌 Verificar si se encontró el usuario
if ($usuario) {
    echo json_encode([
    "nombre" => $usuario["nombre"],
    "foto_perfil" => $usuario["foto_perfil"]
], JSON_UNESCAPED_SLASHES);

} else {
    echo json_encode(["error" => "Usuario no encontrado"]);
}

$conn->close();
?>
