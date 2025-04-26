<?php
header('Content-Type: application/json');

$target_dir = "uploads/";
if (!file_exists($target_dir)) {
    mkdir($target_dir, 0777, true); // Crear la carpeta si no existe
}

$response = array();
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (!isset($_FILES["archivo"]) || !isset($_POST["email"]) || !isset($_POST["tipo"])) {
        $response["success"] = false;
        $response["message"] = "Faltan parámetros";
        echo json_encode($response);
        exit;
    }

    $email = $_POST["email"];
    $tipo = $_POST["tipo"]; // "carnetIdentidad" o "carnetCuidadora"
    $file_name = $tipo . "_" . time() . "_" . basename($_FILES["archivo"]["name"]);
    $target_file = $target_dir . $file_name;

    if (move_uploaded_file($_FILES["archivo"]["tmp_name"], $target_file)) {
        $url = "https://www.cuatroraices.cl/api/uploads/" . $file_name;

        // Guardar en la base de datos
        require_once("conexion.php");
        $query = "UPDATE perfil_usuarios SET $tipo = ? WHERE email = ?";
        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $url, $email);
        if ($stmt->execute()) {
            $response["success"] = true;
            $response["message"] = "Archivo subido correctamente";
            $response["url"] = $url;
        } else {
            $response["success"] = false;
            $response["message"] = "Error al actualizar la BD";
        }
    } else {
        $response["success"] = false;
        $response["message"] = "Error al subir el archivo";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Método no permitido";
}

echo json_encode($response);
?>
