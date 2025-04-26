<?php
include 'conexion.php'; // Archivo con la conexión a la base de datos

header("Content-Type: application/json");

// Solo se permiten solicitudes POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["error" => "Método no permitido"]);
    exit;
}

// Verifica que se envíen los parámetros necesarios: 'rut' y el archivo 'foto'
if (!isset($_POST['rut']) || !isset($_FILES['foto'])) {
    echo json_encode(["error" => "Faltan parámetros (rut y foto son requeridos)"]);
    exit;
}

$rut = $_POST['rut'];
$file = $_FILES['foto'];

// Verifica que la carga del archivo se haya realizado sin errores
if ($file['error'] !== UPLOAD_ERR_OK) {
    echo json_encode(["error" => "Error en la carga del archivo"]);
    exit;
}

// Define el nuevo nombre para la imagen, usando el formato: PERFIL-{rut}.jpg
$newFileName = "PERFIL-" . $rut . ".jpg";

// Define el directorio destino (asegúrate de que sea escribible)
// Por ejemplo, "uploads/" en el mismo directorio donde se encuentra este script
$targetDir = "uploads/";

// Crea el directorio si no existe
if (!is_dir($targetDir)) {
    if (!mkdir($targetDir, 0777, true)) {
        echo json_encode(["error" => "No se pudo crear el directorio destino"]);
        exit;
    }
}

$targetFilePath = $targetDir . $newFileName;

// Mueve el archivo cargado al directorio destino con el nuevo nombre
if (move_uploaded_file($file['tmp_name'], $targetFilePath)) {
    // Actualiza la base de datos. Supongamos que la tabla es wp_perfiles_usuarios y el campo se llama foto_perfil.
    $stmt = $conn->prepare("UPDATE wp_perfil_usuarios SET foto_perfil = ? WHERE rut = ?");
    if (!$stmt) {
        echo json_encode(["error" => "Error en la preparación de la consulta: " . $conn->error]);
        exit;
    }
    $stmt->bind_param("ss", $targetFilePath, $rut);
    if ($stmt->execute()) {
        echo json_encode([
            "error" => false,
            "mensaje" => "Foto actualizada exitosamente",
            "foto" => $targetFilePath
        ]);
    } else {
        echo json_encode(["error" => "Error al actualizar la base de datos"]);
    }
    $stmt->close();
} else {
    echo json_encode(["error" => "Error al mover el archivo"]);
}

$conn->close();
?>
