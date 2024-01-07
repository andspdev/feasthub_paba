<?php

include './config.php';

header('Content-Type: application/json');

$id = $_GET['item_id'];
$pengguna_id = $_GET['pengguna_id'];

$cek_item = "SELECT 
    id, nama_item, kategori, deskripsi, harga, ukuran,
    (
        SELECT url_foto
        FROM thumbnail_items
        WHERE item_id = i.id AND is_thumbnail = 1
        LIMIT 1
    ) thumbnail
FROM `items` i WHERE id = ?";
$cek_item = $pdo->prepare($cek_item);
$cek_item->execute([ $id ]);

$fetch_data = $cek_item->fetch(PDO::FETCH_OBJ);


$is_wishlist = false;
if ($pengguna_id != '')
{
    $cek_wishlist = "SELECT id FROM `wishlists` WHERE item_id = ? AND pengguna_id = ?";
    $cek_wishlist = $pdo->prepare($cek_wishlist);
    $cek_wishlist->execute([ $id, $pengguna_id ]);
    $is_wishlist = $cek_wishlist->rowCount() > 0;
}


$data = [
    'id' => ''
];

if ($cek_item->rowCount() > 0)
    $data = [
        'id' => $fetch_data->id,
        'nama_item' => $fetch_data->nama_item,
        'kategori' => $fetch_data->kategori,
        'deskripsi' => $fetch_data->deskripsi,
        'harga' => $fetch_data->harga,
        'harga_indo' => 'Rp'.mataUangIndo($fetch_data->harga, 0),
        'ukuran' => $fetch_data->ukuran,
        'thumbnail' => 'https://paba.andsp.id/foto/'.$fetch_data->thumbnail,
        'is_wishlist' => $is_wishlist
    ];


echo json_encode([
    'success' => true,
    'detail' => $data
]);

?>