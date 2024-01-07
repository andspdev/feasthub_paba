package com.proyek.paba.feasthub.api.add_items

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {
    @Multipart
    @POST("/insert_item.php")
    fun createPost(
        @Part("nama_item") namaItem: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("ukuran") ukuran: RequestBody,
        @Part("satuan") satuan: RequestBody,
        @Part thumbnail: MultipartBody.Part,
    ): Call<ResponseAdd>
}