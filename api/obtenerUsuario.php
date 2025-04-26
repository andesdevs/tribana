<?php
include 'db_config.php';

header("Content-Type: application/json");

// Conexión a la base de datos
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    error_log("Error en la conexión: " . $conn->connect_error);
    die(json_encode(["error" => "Error en la conexión"]));
}

// Verifica si se envió el email
$email = $_GET['email'] ?? '';
if (empty($email)) {
    error_log("⚠️ Falta el email en la solicitud");
    die(json_encode(["error" => "Falta el email"]));
}

error_log("📩 Email recibido: " . $email);

// Buscar el usuario en la base de datos
$sql = "SELECT nombre, rut, direccion, telefono, user_email AS email FROM wp_perfil_usuarios WHERE user_email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

if ($user) {
    error_log("✅ Usuario encontrado: " . print_r($user, true));

    echo json_encode(["success" => true, "usuario" => $user]);
} else {
    error_log("❌ Usuario no encontrado en la BD para el email: " . $email);
    echo json_encode(["error" => "Usuario no encontrado"]);
}

$stmt->close();
$conn->close();
?>

