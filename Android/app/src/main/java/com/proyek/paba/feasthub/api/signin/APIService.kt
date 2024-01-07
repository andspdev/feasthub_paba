package com.proyek.paba.feasthub.api.signin

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("/masuk.php")
    fun createPost(@Body request: SignInRequest): Call<ResponsePost>
}
