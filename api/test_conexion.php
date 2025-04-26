<?php
include 'conexion.php';

if ($conn) {
    echo json_encode(["success" => "Conexión a la base de datos exitosa"]);
} else {
    echo json_encode(["error" => "Error de conexión"]);
}
?>
