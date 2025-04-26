<?php

include 'conexion.php';

header("Content-Type: application/json");

// Verificar si se envió el RUT
if (!isset($_GET['rut'])) {
    echo json_encode(["error" => "Faltan parámetros"]);
    exit;
}

$rut = $_GET['rut'];

// Consulta para obtener todos los datos del usuario
$sql = "SELECT user_email, rut, nombre, direccion, telefono, ciudad, etiquetas, fecha_registro, carnet_identidad, carnet_cuidadora, foto_perfil 
        FROM wp_perfil_usuarios WHERE rut = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $rut);
$stmt->execute();
$result = $stmt->get_result();

// Verificar si hay resultados
if ($result->num_rows > 0) {
    $usuario = $result->fetch_assoc();
    echo json_encode(["error" => false, "usuario" => $usuario]);
} else {
    echo json_encode(["error" => "Usuario no encontrado"]);
}

// Cerrar conexión
$stmt->close();
$conn->close();
?>
