<?php
include 'db_config.php';

$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    die(json_encode(["error" => "Error en la conexión: " . $conn->connect_error]));
}

// 🔹 Rotar el **Top 3** (Destacadas)
$sqlExpireTop3 = "UPDATE wp_ofertas SET en_top3 = 0 WHERE en_top3 = 1 AND tiempo_rotacion < NOW()";
$conn->query($sqlExpireTop3);

$sqlNewTop3 = "SELECT id FROM wp_ofertas WHERE categoria = 'destacada' AND en_top3 = 0 ORDER BY fecha_publicacion ASC LIMIT 3";
$resultNewTop3 = $conn->query($sqlNewTop3);
while ($row = $resultNewTop3->fetch_assoc()) {
    $conn->query("UPDATE wp_ofertas SET en_top3 = 1, tiempo_rotacion = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE id = " . $row['id']);
}

// 🔹 Rotar el **Top 10 Pro** (puestos 4 al 14)
$sqlResetPro = "UPDATE wp_ofertas SET en_top3 = 0 WHERE categoria = 'pro' AND tiempo_rotacion < NOW()";
$conn->query($sqlResetPro);

$sqlNewPro = "SELECT id FROM wp_ofertas WHERE categoria = 'pro' ORDER BY fecha_publicacion ASC LIMIT 10";
$resultNewPro = $conn->query($sqlNewPro);
$rank = 4;
while ($row = $resultNewPro->fetch_assoc()) {
    $conn->query("UPDATE wp_ofertas SET en_top3 = $rank, tiempo_rotacion = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE id = " . $row['id']);
    $rank++;
}

// 🔹 Rotar el **resto de ofertas (Básicas)**
$sqlResetBasicas = "UPDATE wp_ofertas SET en_top3 = 0 WHERE categoria = 'basica' AND tiempo_rotacion < NOW()";
$conn->query($sqlResetBasicas);

$sqlNewBasicas = "SELECT id FROM wp_ofertas WHERE categoria = 'basica' ORDER BY fecha_publicacion ASC LIMIT 50";
$resultNewBasicas = $conn->query($sqlNewBasicas);
$rank = 15;
while ($row = $resultNewBasicas->fetch_assoc()) {
    $conn->query("UPDATE wp_ofertas SET en_top3 = $rank, tiempo_rotacion = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE id = " . $row['id']);
    $rank++;
}

$conn->close();
echo json_encode(["success" => true, "message" => "Rotación de ofertas completada."]);
?>
