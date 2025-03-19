package com.example.cookup.models

import com.google.firebase.Timestamp

data class Comment(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
