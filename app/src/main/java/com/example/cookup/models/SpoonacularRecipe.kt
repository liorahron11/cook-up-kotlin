package com.example.cookup.models

import com.example.cookup.enums.EIngredientUnit
import com.google.firebase.Timestamp

data class SpoonacularRecipe(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<SpoonacularIngredient>
)

fun SpoonacularRecipe.toAppRecipe(): Recipe {
    return Recipe(
        id = id.toString(),
        title = title,
        image = image,
        instructions = instructions ?: "",
        ingredients = extendedIngredients.map { Ingredient(name = it.name, quantity = it.amount, unit = EIngredientUnit.fromSpoonacularUnit(it.unit.toString()))},
        timestamp = Timestamp.now(),
        senderId = "Spoonacular",
        description = ""
    )
}
