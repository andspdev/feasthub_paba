<?php

include './config.php';

header('Content-Type: application/json');


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    $nama = $data['nama'];
    $password = $data['password'];
    $email = $data['email'];

    $cek_email = "SELECT email FROM `pengguna` WHERE email = ? LIMIT 1";
    $cek_email = $pdo->prepare($cek_email);
    $cek_email->execute([ $email ]);

    $success = false;
    $error = '';
    if ($cek_email->rowCount() > 0)
    {
        $error = 'Email has been used.';
    }
    else
    {
        $password = password_hash($password, PASSWORD_BCRYPT);
        $insert_user = "INSERT INTO `pengguna` SET nama = ?, password = ?, email = ?";
        $insert_user = $pdo->prepare($insert_user);
        $insert_user->execute([ $nama, $password, $email ]);

        $success = true;
    }


    if ($success) {
        echo json_encode([
            'success' => true
        ]);
    }
    else {
        echo json_encode([
            'success' => false,
            'error' => $error
        ]);
    }
}