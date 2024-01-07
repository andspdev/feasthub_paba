<?php

include './config.php';

header('Content-Type: application/json');

$pengguna_id = $_GET['pengguna_id'];

$select_item = "SELECT 
    id, 
    created_at, 
    status_pembayaran, 
    total_harga, 
    total_item, 
    (
        SELECT 
            it.nama_item
        FROM `shopping_cart_items` sci
            INNER JOIN `items` it
                ON it.id = sci.item_id
        WHERE cart_id = sc.id
            LIMIT 1
    ) title,
    (
        SELECT 
        (
            SELECT url_foto
            FROM thumbnail_items
            WHERE item_id = it.id 
                AND is_thumbnail = 1
            LIMIT 1
        ) thumbnail
        FROM `shopping_cart_items` sci
            INNER JOIN `items` it
                ON it.id = sci.item_id
        WHERE cart_id = sc.id
            LIMIT 1
    ) thumbnail
    FROM `shopping_cart` sc
    WHERE pengguna_id = ?
    ORDER BY created_at DESC";
$select_item = $pdo->prepare($select_item);
$select_item->execute([ $pengguna_id ]);


$item_list = [];
if ($select_item->rowCount() > 0)
{
    while($item = $select_item->fetch(PDO::FETCH_OBJ))
    {
        $item_list[] = [
            'id' => $item->id,
            'title' => $item->title ?? '',
            'thumbnail' => $item->thumbnail != '' ? 'https://paba.andsp.id/foto/'.$item->thumbnail : '',
            'total_item' => $item->total_item,
            'total_harga' => 'Rp'.mataUangIndo($item->total_harga, 0),
            'status' => $item->status_pembayaran,
            'dibuat_pada' => date('d-m-Y', strtotime($item->created_at))
        ];
    }
}

echo json_encode([
    'success' => true,
    'data' => $item_list
])

?>