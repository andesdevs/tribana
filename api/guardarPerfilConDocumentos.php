<?php
include 'db_config.php';

header("Content-Type: application/json");

// Conexión a la base de datos
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    die(json_encode(["error" => "Error en la conexión: " . $conn->connect_error]));
}

// Recibir datos del formulario
$nombre = $_POST['nombre'] ?? '';
$rut = $_POST['rut'] ?? '';
$direccion = $_POST['direccion'] ?? '';
$telefono = $_POST['telefono'] ?? '';
$etiquetas = $_POST['etiquetas'] ?? '';
$ciudad = $_POST['ciudad'] ?? '';
$email = $_POST['email'] ?? '';

// Validación de campos obligatorios
if (empty($nombre) || empty($rut) || empty($direccion) || empty($telefono) || empty($email)) {
    die(json_encode(["error" => "Faltan datos obligatorios"]));
}

// Definir directorio de subida (asegúrate de que exista y tenga permisos de escritura)
$uploadDir = 'uploads/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$carnetIdentidadUrl = null;
$carnetCuidadoraUrl = null;

// Limpiar el RUT para conservar dígitos, "k/K" y el guion
$rutLimpio = preg_replace('/[^0-9\-kK]/', '', $rut);

// Procesar archivo de carnet de identidad, si existe
if (isset($_FILES['carnetIdentidad']) && $_FILES['carnetIdentidad']['error'] === UPLOAD_ERR_OK) {
    // Obtener la extensión del archivo
    $extension = pathinfo($_FILES['carnetIdentidad']['name'], PATHINFO_EXTENSION);
    // Formar el nombre de archivo usando el RUT (por ejemplo, CI-15685468-9.jpg)
    $filename = "CI-{$rutLimpio}." . $extension;
    $targetFile = $uploadDir . $filename;
    if (move_uploaded_file($_FILES['carnetIdentidad']['tmp_name'], $targetFile)) {
        $carnetIdentidadUrl = $targetFile;
    }
}

// Procesar archivo de carnet de cuidadora, si existe
if (isset($_FILES['carnetCuidadora']) && $_FILES['carnetCuidadora']['error'] === UPLOAD_ERR_OK) {
    $extension = pathinfo($_FILES['carnetCuidadora']['name'], PATHINFO_EXTENSION);
    $filename = "CUIDADORA-{$rutLimpio}." . $extension;
    $targetFile = $uploadDir . $filename;
    if (move_uploaded_file($_FILES['carnetCuidadora']['tmp_name'], $targetFile)) {
        $carnetCuidadoraUrl = $targetFile;
    }
}

// Preparar la consulta para insertar los datos en la base de datos, incluyendo tipo_cuenta
$sql = "INSERT INTO wp_perfil_usuarios 
        (user_email, rut, nombre, direccion, telefono, etiquetas, ciudad, carnet_identidad, carnet_cuidadora, tipo_cuenta, fecha_registro)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'basico', NOW())";
$stmt = $conn->prepare($sql);
$stmt->bind_param(
    "sssssssss",
    $email,
    $rut,
    $nombre,
    $direccion,
    $telefono,
    $etiquetas,
    $ciudad,
    $carnetIdentidadUrl,
    $carnetCuidadoraUrl
);


if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Perfil guardado con éxito"]);
} else {
    echo json_encode(["error" => "Error al guardar perfil"]);
}

$stmt->close();
$conn->close();
?>
