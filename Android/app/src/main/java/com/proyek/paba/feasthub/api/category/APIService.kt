package com.proyek.paba.feasthub.api.category

import com.proyek.paba.feasthub.api.home.ResponseGet
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/item_category.php")
    fun create(@Query("kategori") kategori: String): Call<ResponseGet>
}