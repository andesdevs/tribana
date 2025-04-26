<?php
include 'db_config.php';

header("Content-Type: application/json");

// Habilitar depuración en desarrollo
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Conectar a la base de datos
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    error_log("Error de conexión: " . $conn->connect_error);
    die(json_encode(["error" => "Error en la conexión a la base de datos"]));
}

// Verificar si se recibió el parámetro rut
if (!isset($_GET['rut'])) {
    die(json_encode(["error" => "Falta el parámetro rut"]));
}

$rut_postulante = $_GET['rut'];
error_log("Consultando datos del RUT: $rut_postulante");

// 📌 1. Contar total de postulaciones del usuario
$sql = "SELECT COUNT(*) as total FROM wp_postulaciones WHERE rut_postulante = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $rut_postulante);
$stmt->execute();
$result = $stmt->get_result();
$total_postulaciones = $result->fetch_assoc()["total"] ?? 0;

// 📌 2. Obtener la última postulación del usuario con título, empresa y sueldo
$sql = "SELECT o.titulo, e.nombre_empresa, o.remuneracion
        FROM wp_postulaciones p
        LEFT JOIN wp_ofertas o ON p.id_oferta = o.id
        LEFT JOIN wp_empresas e ON o.id_empresa = e.id
        WHERE p.rut_postulante = ?
        ORDER BY p.fecha_postulacion DESC
        LIMIT 1";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $rut_postulante);
$stmt->execute();
$result = $stmt->get_result();
$ultima_postulacion = $result->fetch_assoc() ?: null;

// 📌 3. Calcular total ganado en ofertas aceptadas
$sql = "SELECT SUM(o.remuneracion) as total FROM wp_postulaciones p
        LEFT JOIN wp_ofertas o ON p.id_oferta = o.id
        WHERE p.rut_postulante = ? AND p.estado = 'aceptado'";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $rut_postulante);
$stmt->execute();
$result = $stmt->get_result();
$total_ganado = $result->fetch_assoc()["total"] ?? 0;

// 📌 Construir la respuesta JSON
$response = [
    "total_postulaciones" => intval($total_postulaciones),
    "ultima_postulacion" => $ultima_postulacion ? [
        "titulo" => $ultima_postulacion["titulo"],
        "empresa" => $ultima_postulacion["nombre_empresa"],
        "sueldo" => intval($ultima_postulacion["remuneracion"])
    ] : null,
    "total_ganado" => intval($total_ganado)
];

// 📌 Registrar logs para depuración
error_log("Respuesta enviada: " . json_encode($response));

echo json_encode($response);
$conn->close();
?>

