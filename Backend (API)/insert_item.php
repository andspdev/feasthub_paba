<?php

include './config.php';

header('Content-Type: application/json');

$success = false;
$error = "";
if ($_SERVER['REQUEST_METHOD'] == "POST") 
{
    $upload_dir = './foto/';
    $file_name = $_FILES['thumbnail']['name'];
    $file_body = $_FILES['thumbnail']['tmp_name'];
    $file_size = $_FILES['thumbnail']['size'];

    $strtolower = strtolower($file_name);
    $explode = explode('.', $strtolower);
    $file_ext = end($explode);
    $ext_allowed = ['png', 'jpg', 'jpeg'];

    $nama_item = $_POST['nama_item'];
    $kategori = $_POST['kategori'];
    $deskripsi = $_POST['deskripsi'];
    $harga = $_POST['harga'];
    $ukuran = $_POST['ukuran'];
    $satuan = $_POST['satuan'];

    $kat_item = ['drinks', 'food', 'cake', 'ice_cream', 'snacks', 'vegetable', 'meal'];

    if (mb_strlen($nama_item) > 120)
        $error = "Item names must be a maximum of 120 characters.";

    else if (!preg_match('/^\d+$/', $harga)) 
        $error = "The item price must be a number.";

    else if ($harga < 1)
        $error = "The minimum item price is Rp1";

    else if (mb_strlen($ukuran) > 80)
        $error = "Maximum item weight is 80 characters.";
    
    else if (mb_strlen($satuan) > 20)
        $error = "Maximum item unit is 20 characters.";
    
    else if (mb_strlen($deskripsi) > 3000) 
        $error = "Item description must be a maximum of 3000 characters.";

    else if (!in_array($kategori, $kat_item))
        $error = "The entered category is not supported.";

    else if (!in_array($file_ext, $ext_allowed)) 
        $error = "File extension is not supported. ".$file_name;

    else 
    {
        $insert_item = "INSERT INTO `items` SET nama_item = ?, kategori = ?, deskripsi = ?, harga = ?, ukuran = ?, satuan = ?";
        $insert_item = $pdo->prepare($insert_item);
        $insert_item->execute([ 
            $nama_item,
            $kategori,
            $deskripsi,
            $harga,
            $ukuran,
            $satuan
        ]);

        $last_insertId = $pdo->lastInsertId();

        $new_filename = md5(time()).'.'.$file_ext;
        $upload_file = move_uploaded_file($file_body, $upload_dir.'/'.$new_filename);

        if ($upload_file) {
            $insert_thumb = "INSERT INTO `thumbnail_items` SET item_id = ?, url_foto = ?, is_thumbnail = 1";
            $insert_thumb = $pdo->prepare($insert_thumb);
            $insert_thumb->execute([
                $last_insertId,
                $new_filename
            ]);
        }
    }
}

echo json_encode([
    'success' => $success,
    'error' => $error
]);