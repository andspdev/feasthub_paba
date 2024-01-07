<?php

include './config.php';

header('Content-Type: application/json');

$search = $_GET['q'];
$json_decode = json_decode($search);

$item_search = $json_decode->search;
$min_harga = $json_decode->minHarga;
$max_harga = $json_decode->maxHarga;
$kategori = $json_decode->kategori;

$query_where = "nama_item LIKE ?";
$arr_where = [ '%'.$item_search.'%' ];

if (
    (is_numeric($min_harga) && is_numeric($max_harga)) &&
    (($min_harga != "" && $min_harga > 0) && ($max_harga != "" && $max_harga > 0))
) {
    $query_where .= " AND (harga BETWEEN ? AND ?)";
    $arr_where = array_merge($arr_where, [ $min_harga, $max_harga ]);
}

if (is_array($kategori) && count($kategori) > 0) {
    $query_where .= " AND (kategori IN ('".implode("', '", $kategori)."'))";
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
FROM `items` i
WHERE $query_where
ORDER BY rand() LIMIT 5";

$cek_item = $pdo->prepare($cek_item);
$cek_item->execute( $arr_where );

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