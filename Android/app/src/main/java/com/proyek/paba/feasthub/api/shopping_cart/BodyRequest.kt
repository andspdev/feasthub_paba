package com.proyek.paba.feasthub.api.shopping_cart

import com.proyek.paba.feasthub.api.details.DetailCart

data class BodyRequest(
    val pengguna_id: String,
    val alamat: String,
    val harga: String,
    val total_item: String,
    val penerima: String,
    val items_cart: ArrayList<DetailCart>,
)
