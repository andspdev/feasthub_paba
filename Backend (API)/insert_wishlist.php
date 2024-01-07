<?php

include './config.php';

header('Content-Type: application/json');

$insert_id = $_GET['insert_id'];
$pengguna_id = $_GET['pengguna_id'];


$cek_wishlist = "SELECT id FROM wishlists WHERE item_id = ? AND pengguna_id = ?";
$cek_wishlist = $pdo->prepare($cek_wishlist);
$cek_wishlist->execute([ $insert_id, $pengguna_id ]);

if  ($cek_wishlist->rowCount() == 0)
{
    $insert_wish = "INSERT INTO `wishlists` SET item_id = ?, pengguna_id = ?";
    $insert_wish = $pdo->prepare($insert_wish);
    $insert_wish->execute([ $insert_id, $pengguna_id ]);
}

echo json_encode([
    'success' => true
]);