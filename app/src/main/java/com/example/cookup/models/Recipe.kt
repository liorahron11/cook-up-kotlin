package com.example.cookup.models

import android.os.Parcelable
import com.example.cookup.utils.TimestampSerializer
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Recipe(
    val id: String = "",
    @Serializable(with = TimestampSerializer::class)
    val timestamp: Timestamp = Timestamp.now(),
    val senderId: String = "",
    val title: String = "",
    val description: String = "",
    val instructions: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val likes: List<String> = emptyList(),
    val image: String = ""
) : Parcelable
