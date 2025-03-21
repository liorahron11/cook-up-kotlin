package com.example.cookup.interfaces

import com.example.cookup.models.SpoonacularResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String
    ): SpoonacularResponse
}