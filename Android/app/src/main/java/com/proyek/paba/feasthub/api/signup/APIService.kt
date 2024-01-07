package com.proyek.paba.feasthub.api.signup

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("/tambah_user.php")
    fun createPost(@Body request: SignUpRequest): Call<ResponsePost>
}
