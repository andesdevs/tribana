<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

include 'conexion.php'; // Archivo de conexión a la base de datos

// Verificar si el servidor está recibiendo la solicitud correctamente
$log_file = "debug_log.txt";
file_put_contents($log_file, "Solicitud recibida\n", FILE_APPEND);

// Verificar que la solicitud sea POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    file_put_contents($log_file, "Método POST detectado\n", FILE_APPEND);

    // Verificar si el parámetro 'email' está presente
    if (!isset($_POST['email'])) {
        file_put_contents($log_file, "Falta el parámetro email\n", FILE_APPEND);
        echo json_encode(["error" => "Falta el parámetro email"]);
        exit;
    }

    $email = trim($_POST['email']);
    file_put_contents($log_file, "Email recibido: $email\n", FILE_APPEND);

    // ❌ ERROR ANTERIOR: Estaba buscando en 'perfil_usuario'
    // ✅ SOLUCIÓN: Ahora usamos 'wp_perfil_usuario'
    $stmt = $conn->prepare("SELECT COUNT(*) as total FROM wp_perfil_usuarios WHERE user_email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    $response = ["tienePerfil" => $row['total'] > 0];
    echo json_encode($response);
} else {
    file_put_contents($log_file, "Solicitud no es POST\n", FILE_APPEND);
    echo json_encode(["error" => "Método no permitido"]);
}
?>
