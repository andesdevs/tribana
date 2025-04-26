<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'conexion.php'; // Asegura que este archivo existe y tiene la conexión correcta

header("Content-Type: application/json");

// Verificar si la conexión a la BD está definida en `conexion.php`
if (!isset($conn) || $conn->connect_error) {
    die(json_encode(["error" => "Conexión fallida: " . ($conn->connect_error ?? "Conexión no establecida")]));
}

// CONSULTA A LA BASE DE DATOS
$sql = "SELECT nombre_etiqueta FROM wp_etiquetas";
$result = $conn->query($sql);

// Verificar si la consulta falló
if (!$result) {
    die(json_encode(["error" => "Error en la consulta SQL: " . $conn->error]));
}

$etiquetas = [];
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $etiquetas[] = $row['nombre_etiqueta'];
    }
}

// RESPUESTA JSON con depuración
if (empty($etiquetas)) {
    echo json_encode(["error" => "No se encontraron etiquetas en la base de datos"]);
} else {
    echo json_encode(["etiquetas" => $etiquetas]);
}

$conn->close();
?>
