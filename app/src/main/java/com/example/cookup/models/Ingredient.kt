package com.example.cookup.models

import android.os.Parcelable
import com.example.cookup.enums.EIngredientUnit
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Ingredient(
    val quantity: Double,
    val unit: EIngredientUnit?,
    val name: String
) : Parcelable{

    constructor() : this(0.0, null, "")

    companion object {
        fun fromMap(map: Map<String, Any>): Ingredient {
            return Ingredient(
                name = map["name"] as? String ?: "",
                quantity = (map["quantity"] as? Number)?.toDouble() ?: 0.0,
                unit = map["unit"] as? EIngredientUnit ?: null
            )
        }
    }
}

