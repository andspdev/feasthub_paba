<?php

include './config.php';

header('Content-Type: application/json');


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    $email = $data['email'];
    $password = $data['password'];

    
    $select_user = "SELECT id, nama, email, password, created_at FROM `pengguna` WHERE email = ? LIMIT 1";
    $select_user = $pdo->prepare($select_user);
    $select_user->execute([ $email ]);

    $fetch_user = '';
    $count_user = $select_user->rowCount();
    $fetch_user = $count_user > 0 ?  $select_user->fetch(PDO::FETCH_OBJ) : '';
    
    $error = '';
    $output_json = '';
    $success = false;

    if ($select_user->rowCount() == 0)
        $error = 'Email could not be found.';

    else if ($password == '' || ($count_user > 0 && 
    !password_verify($password, $fetch_user->password)))
        $error = 'Incorrect password typed.';

    else {
        $success = true;
    }

    if ($success)
    {
        echo json_encode([
            'success' => true,
            'detail' => [
                'id' => $fetch_user->id,
                'name' => $fetch_user->nama,
                'email' => $fetch_user->email,
                'tanggal_bergabung' => date('Y-m-d H:i', strtotime($fetch_user->created_at))
            ]
        ]);
    }
    else
    {
        echo json_encode([
            'success' => false,
            'error' =>  $error
        ]);
    }
}