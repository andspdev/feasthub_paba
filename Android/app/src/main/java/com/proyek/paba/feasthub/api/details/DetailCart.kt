package com.proyek.paba.feasthub.api.details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DetailCart(
    val id: Int,
    val nama_item: String,
    val harga: Int,
    val harga_indo: String,
    val thumbnail: String,
    var total_cart: Int
) : Parcelable
