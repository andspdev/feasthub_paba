package com.proyek.paba.feasthub.api.edit_user


data class EditUserRequest (
    val id: String,
    val email: String,
    val name: String,
    val password: String
)