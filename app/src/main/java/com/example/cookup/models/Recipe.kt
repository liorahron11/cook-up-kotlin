package com.example.cookup.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val senderId: String = "",
    val title: String = "",
    val description: String = "",
    val instructions: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val likes: List<String> = emptyList(),
    val image: String = ""
) : Parcelable
