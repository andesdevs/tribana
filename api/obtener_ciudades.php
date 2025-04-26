<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'conexion.php';

header("Content-Type: application/json");

if (!isset($conn) || $conn->connect_error) {
    die(json_encode([])); // Devolver array vacío si hay error
}

$sql = "SELECT comuna FROM wp_comunas ORDER BY comuna ASC";
$result = $conn->query($sql);

$ciudades = [];
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $ciudades[] = $row['comuna'];
    }
}

echo json_encode($ciudades); // 🔹 Devuelve un array en JSON

$conn->close();
?>
