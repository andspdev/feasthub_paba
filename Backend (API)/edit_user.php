<?php

include './config.php';

header('Content-Type: application/json');


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    $id = $data['id'];
    $email = $data['email'];
    $nama = $data['name'];
    $password = $data['password'];

    $cek_email = "SELECT email FROM `pengguna` WHERE email = ? AND id != ?";
    $cek_email = $pdo->prepare($cek_email);
    $cek_email->execute([ $email, $id ]);

    if ($cek_email->rowCount() > 0)
        echo json_encode([ 'success' => false, 'error' => 'Email has been used.' ]);

    else
    {
        $update_data = "UPDATE `pengguna` SET nama = ?, email = ?";

        if ($password != '')
        {
            $update_data .= ", password = ?";
        }

        $update_data .= " WHERE id = ?";

        if ($password != '')
            $exec_array = [ $nama, $email, password_hash($password, PASSWORD_BCRYPT), $id ];
        else
            $exec_array = [ $nama, $email, $id ];

        $update_data = $pdo->prepare($update_data);
        $update_data->execute($exec_array);


        if ($update_data)
            echo json_encode([ 'success' => true ]);
        else
            echo json_encode([ 'success' => true, 'error' => 'An error occurred while updating data.' ]);
    }
}