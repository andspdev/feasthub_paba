<?php

include './config.php';

header('Content-Type: application/json');

$pengguna_id = $_GET['pengguna_id'];
$delete_id = $_GET['delete_id'];

$delete = "DELETE FROM `wishlists` WHERE item_id = ? AND pengguna_id = ?";
$delete = $pdo->prepare($delete);
$delete->execute([ $delete_id, $pengguna_id ]);

echo json_encode([
    'success' => true
])

?>