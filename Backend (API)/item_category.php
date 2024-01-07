<?php

include './config.php';

header('Content-Type: application/json');

$kategori = $_GET['kategori'];

$arr_exec = [ $kategori ];
$cond = "WHERE kategori = ?";
if ($kategori == 'all')
{
    $arr_exec = [];
    $cond = "";
}

$cek_item = 
"SELECT 
    id, nama_item nama, harga, ukuran, satuan,
    (
        SELECT url_foto
        FROM thumbnail_items
        WHERE item_id = i.id AND is_thumbnail = 1
        LIMIT 1
    ) thumbnail
FROM `items` i $cond
ORDER BY nama_item ASC";

$cek_item = $pdo->prepare($cek_item);
$cek_item->execute($arr_exec);

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