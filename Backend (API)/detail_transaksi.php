<?php

include './config.php';

header('Content-Type: application/json');

$transaksi_id = $_GET['transaksi_id'];
$pengguna_id = $_GET['pengguna_id'];

$select_transaksi = 
"SELECT 
    id,
    no_resi, 
    penerima,
    alamat,
    total_item, 
    harga, 
    harga_fee, 
    total_harga,
    paid_on,
    status_pembayaran,
    created_at,
    virtual_account
FROM `shopping_cart`
WHERE 
id = ? 
AND pengguna_id = ?";
$select_transaksi = $pdo->prepare($select_transaksi);
$select_transaksi->execute([ $transaksi_id, $pengguna_id ]);

$data = "";
if ($select_transaksi->rowCount() > 0)
{
    $fetch_data = $select_transaksi->fetch(PDO::FETCH_OBJ);

    $produk_items = [];
    $cek_arr = "SELECT
        item_id,
        i.nama_item,
        (
            SELECT url_foto
            FROM thumbnail_items
            WHERE item_id = i.id AND is_thumbnail = 1
            LIMIT 1
        ) thumbnail,
        harga,
        total total_beli
        FROM `shopping_cart_items` sci
        INNER JOIN `items` i
        ON i.id = sci.item_id
        WHERE sci.cart_id = ?
        ORDER BY sci.id ASC";
    $cek_arr = $pdo->prepare($cek_arr);
    $cek_arr->execute([ $fetch_data->id ]);

    if ($cek_arr->rowCount() > 0)
    {
        while($item = $cek_arr->fetch(PDO::FETCH_OBJ))
            $produk_items[] = [
                'item_id' => $item->item_id,
                'nama_item' => $item->nama_item,
                'thumbnail' => 'https://paba.andsp.id/foto/'.$item->thumbnail,
                'harga' => $item->harga,
                'total_beli' => $item->total_beli,
                'total_harga' => $item->harga * $item->total_beli
            ];
    }

    $data = [
        'status' => $fetch_data->status_pembayaran,
	'tanggal_pembelian' => $fetch_data->created_at,
        'dibayar_pada' => $fetch_data->paid_on ?? '',
        'no_resi' => $fetch_data->no_resi,
        'penerima' => $fetch_data->penerima,
        'alamat' => $fetch_data->alamat,
        'total_item' => $fetch_data->total_item,
        'harga' => $fetch_data->harga,
        'harga_fee' => $fetch_data->harga_fee,
        'total_harga' => $fetch_data->total_harga,
        'produk_items' => $produk_items,
	'virtual_account' => $fetch_data->virtual_account
    ];
}

echo json_encode([
    'success' => true,
    'data' => $data
])

?>