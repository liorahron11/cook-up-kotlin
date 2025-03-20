package com.example.cookup.models

import com.example.cookup.enums.EIngredientUnit

data class Ingredient(
    val quantity: Number,
    val unit: EIngredientUnit,
    val name: String
)
