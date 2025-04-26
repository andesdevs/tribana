<?php
include 'conexion.php';

header("Content-Type: application/json");

// Verificar si la solicitud es POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["error" => "Método no permitido"]);
    exit;
}

// Verificar si se recibió el RUT
if (!isset($_POST['rut'])) {
    echo json_encode(["error" => "Falta el RUT del usuario"]);
    exit;
}

$rut = $_POST['rut'];
$nombre = $_POST['nombre'] ?? null;
$direccion = $_POST['direccion'] ?? null;
$telefono = $_POST['telefono'] ?? null;
$ciudad = $_POST['ciudad'] ?? null;
$etiquetas = $_POST['etiquetas'] ?? null;

// Inicializar array de actualizaciones
$updates = [];
$params = [];

// Construir la consulta dinámicamente según los datos recibidos
if ($nombre) { $updates[] = "nombre = ?"; $params[] = $nombre; }
if ($direccion) { $updates[] = "direccion = ?"; $params[] = $direccion; }
if ($telefono) { $updates[] = "telefono = ?"; $params[] = $telefono; }
if ($ciudad) { $updates[] = "ciudad = ?"; $params[] = $ciudad; }
if ($etiquetas) { $updates[] = "etiquetas = ?"; $params[] = $etiquetas; }

// Manejo de archivos (carnet_identidad, carnet_cuidadora)
$uploadDir = "uploads/";
foreach (['carnet_identidad', 'carnet_cuidadora'] as $campo) {
    if (isset($_FILES[$campo]) && $_FILES[$campo]['error'] === UPLOAD_ERR_OK) {
        $extension = pathinfo($_FILES[$campo]['name'], PATHINFO_EXTENSION);
        $filename = strtoupper($campo) . "-$rut.$extension";
        $filePath = $uploadDir . $filename;

        if (move_uploaded_file($_FILES[$campo]['tmp_name'], $filePath)) {
            $updates[] = "$campo = ?";
            $params[] = $filePath;
        }
    }
}

// Si no hay nada que actualizar, finalizar
if (empty($updates)) {
    echo json_encode(["error" => "No se enviaron datos para actualizar"]);
    exit;
}

// Agregar el RUT al final de los parámetros
$params[] = $rut;

// Crear consulta dinámica
$sql = "UPDATE wp_perfil_usuarios SET " . implode(", ", $updates) . " WHERE rut = ?";
$stmt = $conn->prepare($sql);

// Construir la lista de tipos de datos para bind_param
$tipos = str_repeat("s", count($params));
$stmt->bind_param($tipos, ...$params);

// Ejecutar consulta
if ($stmt->execute()) {
    echo json_encode(["error" => false, "mensaje" => "Perfil actualizado exitosamente"]);
} else {
    echo json_encode(["error" => "Error al actualizar el perfil"]);
}

// Cerrar conexión
$stmt->close();
$conn->close();
?>
