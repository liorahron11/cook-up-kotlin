package com.example.cookup.models

import com.example.cookup.enums.EIngredientUnit

data class Ingredient(
    val quantity: Int,
    val unit: EIngredientUnit?,
    val name: String
){

    constructor() : this(0, null, "")

    companion object {
        fun fromMap(map: Map<String, Any>): Ingredient {
            return Ingredient(
                name = map["name"] as? String ?: "",
                quantity = (map["quantity"] as? Number)?.toInt() ?: 0,
                unit = map["unit"] as? EIngredientUnit ?: null
            )
        }
    }
}

