package com.example.cookup.models

data class RecipeWithUser(
    val recipe: Recipe,
    val user: User?
)
