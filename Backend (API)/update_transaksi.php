<?php

include './config.php';

header('Content-Type: application/json');

$transaksi_id = $_GET['transaksi_id'];
$pengguna_id = $_GET['pengguna_id'];

$time = time();
$paid_on = date('Y-m-d H:i:s', $time);
$update_transaksi = "UPDATE `shopping_cart` SET status_pembayaran = 'selesai', paid_on = ? WHERE id = ? AND pengguna_id = ?";
$update_transaksi = $pdo->prepare($update_transaksi);
$update_transaksi->execute([ $paid_on, $transaksi_id, $pengguna_id ]);

echo json_encode([
    'success' => true,
    'paid_on' => $paid_on
]);