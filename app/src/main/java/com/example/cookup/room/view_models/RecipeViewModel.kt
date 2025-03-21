package com.example.cookup.room.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.room.repositories.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(application, RecipeRemoteDataSource())

    fun insertRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun getRecipe(recipeId: String, callback: (RecipeEntity?) -> Unit) {
        viewModelScope.launch {
            val recipe = repository.getRecipe(recipeId)
            callback(recipe)
        }
    }

    fun getAllRecipes(callback: (Flow<List<RecipeEntity>>) -> Unit) {
        viewModelScope.launch {
            val recipes = repository.getAllRecipes()
            callback(recipes)
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun getRecipesBySenderId(senderId: String, callback: (List<RecipeEntity>) -> Unit) {
        viewModelScope.launch {
            val recipes = repository.getRecipesBySenderId(senderId)
            callback(recipes)
        }
    }

    fun insertRecipes(recipes: List<RecipeEntity>) {
        viewModelScope.launch {
            repository.insertRecipes(recipes)
        }
    }
}
