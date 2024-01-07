package com.proyek.paba.feasthub.api.shopping_cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("/insert_shopping_cart.php")
    fun insert(
        @Body
        request: BodyRequest
    ): Call<ResponsePost>

}