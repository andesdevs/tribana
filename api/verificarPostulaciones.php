<?php
include 'db_config.php';

header("Content-Type: application/json");

$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    die(json_encode(["error" => "Error en la conexión: " . $conn->connect_error]));
}

// Obtener el email o el RUT del usuario
$email = $_GET['email'] ?? '';
$rut = $_GET['rut'] ?? '';

if (empty($email) && empty($rut)) {
    die(json_encode(["error" => "Falta el email o RUT del usuario"]));
}

// Consultar las ofertas a las que el usuario se postuló
$sql = "SELECT id_oferta FROM wp_postulaciones WHERE email_postulante = ? OR rut_postulante = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $email, $rut);
$stmt->execute();
$result = $stmt->get_result();

$postulaciones = [];
while ($row = $result->fetch_assoc()) {
    $postulaciones[] = $row['id_oferta'];
}

// Devolver las postulaciones en JSON
echo json_encode(["success" => true, "postulaciones" => $postulaciones]);

$stmt->close();
$conn->close();
?>
