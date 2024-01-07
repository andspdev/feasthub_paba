package com.proyek.paba.feasthub.api.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemsCartArray(
    val id: String,
    val nama: String,
    val harga: String,
    val ukuran: String,
    val satuan: String,
    val thumbnail: String,
    val jumlahItem : String
) : Parcelable
