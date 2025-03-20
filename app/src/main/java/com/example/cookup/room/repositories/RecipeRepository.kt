package com.example.cookup.room.repositories

import android.content.Context
import com.example.cookup.room.databases.RecipeDatabase
import com.example.cookup.room.entities.RecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(context: Context) {
    private val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()

    suspend fun insertRecipe(recipe: RecipeEntity) {
        withContext(Dispatchers.IO) {
            recipeDao.insertRecipe(recipe)
        }
    }

    suspend fun getRecipe(recipeId: String): RecipeEntity? {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipe(recipeId)
        }
    }

    suspend fun getAllRecipes(): List<RecipeEntity> {
        return withContext(Dispatchers.IO) {
            recipeDao.getAllRecipes()
        }
    }

    suspend fun deleteRecipe(recipe: RecipeEntity) {
        withContext(Dispatchers.IO) {
            recipeDao.deleteRecipe(recipe)
        }
    }

    suspend fun getRecipesBySenderId(senderId: String): List<RecipeEntity> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesBySenderId(senderId)
        }
    }

    suspend fun insertRecipes(recipes: List<RecipeEntity>) {
        withContext(Dispatchers.IO) {
            recipeDao.insertRecipes(recipes)
        }
    }
}
