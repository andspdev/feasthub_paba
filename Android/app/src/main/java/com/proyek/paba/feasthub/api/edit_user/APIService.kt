package com.proyek.paba.feasthub.api.edit_user

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("/edit_user.php")
    fun createPost(@Body request: EditUserRequest): Call<ResponsePost>
}
