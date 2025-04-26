<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include 'conexion.php';

$data = json_decode(file_get_contents("php://input"));

if (
    isset($data->user_email) &&
    isset($data->nombre) &&
    isset($data->telefono) &&
    isset($data->ciudad) &&
    isset($data->etiquetas)
) {
    $user_email = $data->user_email;
    $nombre = $data->nombre;
    $telefono = $data->telefono;
    $ciudad = $data->ciudad;
    $etiquetas = $data->etiquetas;

    // Verificar si ya existe el perfil
    $check_query = "SELECT * FROM wp_perfil_usuarios WHERE user_email = ?";
    $stmt = $conexion->prepare($check_query);
    $stmt->bind_param("s", $user_email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        echo json_encode(["status" => "existe", "message" => "El perfil ya existe."]);
    } else {
        $query = "INSERT INTO wp_perfil_usuarios (user_email, nombre, telefono, ciudad, etiquetas, tipo_cuenta) 
                  VALUES (?, ?, ?, ?, ?, 'basica')";
        $stmt = $conexion->prepare($query);
        $stmt->bind_param("sssss", $user_email, $nombre, $telefono, $ciudad, $etiquetas);

        if ($stmt->execute()) {
            echo json_encode(["status" => "ok", "message" => "Perfil creado exitosamente."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Error al crear perfil."]);
        }
    }

    $stmt->close();
    $conexion->close();
} else {
    echo json_encode(["status" => "error", "message" => "Faltan datos requeridos."]);
}
