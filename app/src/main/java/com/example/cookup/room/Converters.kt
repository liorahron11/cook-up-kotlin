package com.example.cookup.room

import androidx.room.TypeConverter
import com.example.cookup.models.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientsList(value: List<Ingredient>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIngredientsList(value: String): List<Ingredient> {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromLikesList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLikesList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}
