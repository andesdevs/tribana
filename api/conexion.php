<?php
include 'db_config.php'; // Incluye la configuración

// Crear la conexión
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

// Verificar conexión
if ($conn->connect_error) {
    die(json_encode(["error" => "Conexión fallida: " . $conn->connect_error]));
}

mysqli_set_charset($conn, "utf8"); // Asegurar que la codificación sea UTF-8
?>
