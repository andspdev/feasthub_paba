package com.proyek.paba.feasthub.api.wishlist

import com.proyek.paba.feasthub.api.home.ResponseGet
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/item_wishlist.php")
    fun create(
        @Query("pengguna_id") pengguna_id: String,
        @Query("search") search: String = ""
    ): Call<ResponseGet>

    @GET("/delete_wishlist.php")
    fun delete(
        @Query("delete_id") delete_id: String,
        @Query("pengguna_id") pengguna_id: String
    ): Call<DeleteResponse>

    @GET("/insert_wishlist.php")
    fun insert(
        @Query("insert_id") insert_id: String,
        @Query("pengguna_id") pengguna_id: String
    ): Call<InsertResponse>
}