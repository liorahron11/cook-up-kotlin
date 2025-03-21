package com.example.cookup.room.repositories

import android.content.Context
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.databases.RecipeDatabase
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.utils.toEntity
import com.example.cookup.utils.toRecipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecipeRepository(context: Context, private val remoteDataSource: RecipeRemoteDataSource) {
    private val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val recipeCollection = firestore.collection("recipes")

    /**
     * Flow of all recipes from Room cache (updated after fetch from Firestore).
     */
    val recipesFlow: Flow<List<Recipe>> = recipeDao.getAllRecipes()
        .map { it.map { entity -> entity.toRecipe() } }

    /**
     * Fetches latest recipes from Firestore and updates the local cache.
     */
    suspend fun refreshRecipesFromRemote() {
        remoteDataSource.fetchRecipes()
            .catch { it.printStackTrace() }
            .collect { remoteRecipes ->
                val entities = remoteRecipes.map { it.toEntity() }
                recipeDao.insertRecipes(entities)
            }
    }


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
            recipeDao.getAllRecipes() as List<RecipeEntity>
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

    val recipesWithUserFlow: Flow<List<RecipeWithUser>> = recipesFlow
        .mapLatest { recipes ->
            recipes.map { recipe ->
                val user = remoteDataSource.getCachedUser(recipe.senderId)
                RecipeWithUser(recipe, user)
            }
        }
}
