package com.example.cookup.models

import com.google.firebase.Timestamp

data class Recipe(
    val id: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val senderId: String = "",
    val title: String = "",
    val description: String = "",
    val instructions: String = "",
    val comments: List<Comment> = emptyList(),
    val likes: List<String> = emptyList(),
    val image: String = ""
)
