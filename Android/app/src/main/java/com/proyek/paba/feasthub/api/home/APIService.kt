package com.proyek.paba.feasthub.api.home

import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("/item_beranda.php")
    fun create(): Call<ResponseGet>
}