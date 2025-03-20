package com.example.cookup.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey() val id: String = "",
    val username: String,
    val email: String,
    val profileImageUrl: String? = null
)