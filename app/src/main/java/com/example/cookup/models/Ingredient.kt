package com.example.cookup.models

import android.os.Parcelable
import com.example.cookup.enums.EIngredientUnit
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Ingredient(
    val quantity: Int,
    val unit: EIngredientUnit?,
    val name: String
) : Parcelable{

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

