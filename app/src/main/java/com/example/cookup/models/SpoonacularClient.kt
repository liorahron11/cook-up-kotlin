package com.example.cookup.models

import com.example.cookup.interfaces.SpoonacularApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpoonacularClient {
    private const val BASE_URL = "https://api.spoonacular.com/"

    fun create(): SpoonacularApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SpoonacularApi::class.java)
    }
}
