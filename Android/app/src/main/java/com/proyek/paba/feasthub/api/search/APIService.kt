package com.proyek.paba.feasthub.api.search

import com.proyek.paba.feasthub.api.home.ResponseGet
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/item_search.php")
    fun create(@Query("q") q: String): Call<ResponseGet>
}