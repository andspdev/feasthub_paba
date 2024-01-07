package com.proyek.paba.feasthub.api.transaction

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/item_transactions.php")
    fun getData(
        @Query("pengguna_id") pengguna_id: String
    ): Call<ResponseGet>
}