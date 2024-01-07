package com.proyek.paba.feasthub.api.detail_transaction

data class Data(
    val status: String,
    val tanggal_pembelian: String,
    val dibayar_pada: String,
    val no_resi: String,
    val penerima: String,
    val alamat: String,
    val total_item: String,
    val harga: String,
    val harga_fee: String,
    val total_harga: String,
    val produk_items: ArrayList<ProdukItems>,
    val virtual_account: String
)
