<?php

include './config.php';

header('Content-Type: application/json');

$pengguna_id = $_GET['pengguna_id'];
$search = $_GET['search'];


$cek_item = 
"SELECT 
i.id, nama_item nama, harga, ukuran, satuan,
(
        SELECT url_foto
        FROM thumbnail_items
        WHERE item_id = i.id AND is_thumbnail = 1
        LIMIT 1
    ) thumbnail
FROM `wishlists` w
INNER JOIN `items` i
ON i.id = w.item_id
WHERE
w.pengguna_id = ? AND
i.nama_item LIKE ?
ORDER BY created_at ASC";
$cek_item = $pdo->prepare($cek_item);
$cek_item->execute([ $pengguna_id, '%'.$search.'%' ]);

$array_all = [];

if ($cek_item->rowCount() > 0)
{
    while($item = $cek_item->fetch(PDO::FETCH_OBJ))
    {
        $array_all[] = [
            'id' => $item->id,
            'nama' => $item->nama,
            'harga' => 'Rp'.mataUangIndo($item->harga, 0),
            'ukuran' => $item->ukuran,
            'satuan' => $item->satuan,
            'thumbnail' => 'https://paba.andsp.id/foto/'.$item->thumbnail
        ];
    }
}

echo json_encode([
    'success' => true,
    'data' => $array_all
]);

?>

