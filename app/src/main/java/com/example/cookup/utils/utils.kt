package com.example.cookup.utils

import com.example.cookup.models.*
import com.example.cookup.room.entities.RecipeEntity
import com.google.firebase.Timestamp
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.Date

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        timestamp = timestamp.toDate().time,
        senderId = senderId,
        title = title,
        description = description,
        instructions = instructions,
        ingredients = json.encodeToString(ListSerializer(Ingredient.serializer()), ingredients),
        likes = json.encodeToString(ListSerializer(String.serializer()), likes),
        image = image
    )
}

fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = id,
        timestamp = Timestamp(Date(timestamp)),
        senderId = senderId,
        title = title,
        description = description,
        instructions = instructions,
        ingredients = json.decodeFromString(ListSerializer(Ingredient.serializer()), ingredients),
        likes = json.decodeFromString(ListSerializer(String.serializer()), likes),
        image = image
    )
}
