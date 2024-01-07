package com.proyek.paba.feasthub.api.details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Detail(
    val id: Int,
    val nama_item: String,
    val kategori: String,
    val deskripsi: String,
    val harga: Int,
    val harga_indo: String,
    val ukuran: String,
    val thumbnail: String,
    var is_wishlist: Boolean
) : Parcelable
