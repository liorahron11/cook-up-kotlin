package com.example.cookup.room.entities

import androidx.room.*

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val senderId: String,
    val title: String,
    val description: String,
    val instructions: String,
    val ingredients: String,
    val likes: String,
    val image: String
)
