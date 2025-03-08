package com.example.cookup.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
)
