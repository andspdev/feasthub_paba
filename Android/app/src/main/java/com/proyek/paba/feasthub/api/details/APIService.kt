package com.proyek.paba.feasthub.api.details

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/item_detail.php")
    fun create(): Call<ResponseGetDetails>

    @GET("/item_detail.php")
    fun getDetails(
        @Query("item_id") item_id: String,
        @Query("pengguna_id") pengguna_id : String,) : Call<ResponseGetDetails>
}