<?php
include 'db_config.php';

$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if ($conn->connect_error) {
    die(json_encode(["error" => "Error en la conexión: " . $conn->connect_error]));
}

// 🔹 Ordenamiento dinámico con rotación dentro de cada categoría
$sql = "SELECT 
            o.id, 
            o.titulo, 
            o.descripcion, 
            o.descripcion_larga, 
            o.remuneracion, 
            o.habilidades, 
            o.ubicacion, 
            o.modalidad, 
            o.estado, 
            o.fecha_publicacion, 
            o.id_empresa, 
            e.nombre_empresa, 
            o.categoria
        FROM wp_ofertas o
        LEFT JOIN wp_empresas e ON o.id_empresa = e.id  
        ORDER BY 
            CASE 
                WHEN o.categoria = 'destacada' THEN 1  
                WHEN o.categoria = 'pro' THEN 2        
                ELSE 3                               
            END,
            RAND()";  

$result = $conn->query($sql);

$ofertas = [];
while ($row = $result->fetch_assoc()) {
    $ofertas[] = $row;
}

echo json_encode($ofertas);
$conn->close();
?>

