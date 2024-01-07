package com.proyek.paba.feasthub.api.detail_transaction

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/detail_transaksi.php")
    fun get_data(
        @Query("transaksi_id") transaksi_id: String,
        @Query("pengguna_id") pengguna_id: String
    ): Call<ResponseGet>

    @GET("/update_transaksi.php")
    fun update_status(
        @Query("transaksi_id") transaksi_id: String,
        @Query("pengguna_id") pengguna_id: String
    ): Call<ResponseUpdate>
}