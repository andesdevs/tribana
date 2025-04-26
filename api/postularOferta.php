<?php
include 'db_config.php';

header("Content-Type: application/json");

// Conexión a la base de datos
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    die(json_encode(["error" => "Error en la conexión: " . $conn->connect_error]));
}

// Recibir datos de la solicitud
$id_oferta = $_POST['id_oferta'] ?? '';
$rut_postulante = $_POST['rut_postulante'] ?? '';
$email_postulante = $_POST['email_postulante'] ?? '';

// Verificación de datos obligatorios
if (empty($id_oferta) || empty($rut_postulante) || empty($email_postulante)) {
    die(json_encode(["error" => "Faltan datos obligatorios"]));
}

// ✅ Verificar si el usuario ya postuló a esta oferta
$checkQuery = "SELECT COUNT(*) as total FROM wp_postulaciones WHERE id_oferta = ? AND rut_postulante = ?";
$stmtCheck = $conn->prepare($checkQuery);
$stmtCheck->bind_param("is", $id_oferta, $rut_postulante);
$stmtCheck->execute();
$resultCheck = $stmtCheck->get_result();
$row = $resultCheck->fetch_assoc();

if ($row['total'] > 0) {
    echo json_encode(["error" => "Ya has postulado a esta oferta"]);
    exit;
}

// ✅ Insertar la postulación si no existe
$sql = "INSERT INTO wp_postulaciones (id_oferta, rut_postulante, email_postulante) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iss", $id_oferta, $rut_postulante, $email_postulante);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Postulación realizada con éxito"]);
} else {
    echo json_encode(["error" => "Error al registrar la postulación"]);
}

$stmt->close();
$conn->close();
?>

