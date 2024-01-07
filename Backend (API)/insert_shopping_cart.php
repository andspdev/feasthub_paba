<?php

include './config.php';

header('Content-Type: application/json');

$success = false;
$last_insert = "";

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    $pengguna_id = $data['pengguna_id'];
    $alamat = $data['alamat'];
    $harga = $data['harga'];
    $total_item = $data['total_item'];
    $penerima = $data['penerima'];
    $items_cart = $data['items_cart'];

    $harga_fee = 16000;


    if (count($items_cart) > 0) {
        $total_harga = $harga + $harga_fee;

        $insert_transaksi = "INSERT INTO `shopping_cart` 
        SET 
        pengguna_id = ?, 
        harga = ?,
        harga_fee = ?,
        total_harga = ?,
        total_item = ?,
        penerima = ?,
        alamat = ?,
        status_pembayaran = 'belum'";
        $insert_transaksi = $pdo->prepare($insert_transaksi);
        $insert_transaksi->execute([
            $pengguna_id,
            $harga,
            $harga_fee,
            $total_harga,
            $total_item,
            $penerima,
            $alamat
        ]);

        $last_insert = $pdo->lastInsertId();

        $success = true;
        foreach($items_cart as $items) {
            $insert_items = "INSERT INTO `shopping_cart_items` SET item_id = ?, cart_id = ?, total = ?";
            $insert_items = $pdo->prepare($insert_items);
            $insert_items->execute([
                $items['id'],
                $last_insert,
                $items['total_cart']
            ]);
        }
    }
}

echo json_encode([
    'success' => $success,
    'last_id' => $last_insert
])

?>