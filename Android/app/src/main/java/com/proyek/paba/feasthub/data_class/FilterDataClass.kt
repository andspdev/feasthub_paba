package com.proyek.paba.feasthub.data_class

data class FilterDataClass (
    val search: String,
    val minHarga: String,
    val maxHarga: String,
    val kategori: ArrayList<String>
)