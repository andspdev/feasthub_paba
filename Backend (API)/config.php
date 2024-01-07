<?php

// error_reporting(0);

try {
    $pdo = new PDO("mysql:host=localhost;dbname=paba", "root", "");
} catch(PDOException $e) {
    die('gagal terhubung ke database');
}

function mataUangIndo($angka, $per = 2)
{
    if (!preg_match("/^([0-9]+)$/", $angka))
        return "-";

    return number_format($angka, $per, ',', '.');
}

?>