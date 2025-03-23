package com.example.cookup.room.repositories

import android.content.Context
import android.util.Log
import com.example.cookup.interfaces.SpoonacularApi
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.models.User
import com.example.cookup.models.toAppRecipe
import com.example.cookup.models.SpoonacularClient
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.databases.RecipeDatabase
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.utils.toEntity
import com.example.cookup.utils.toRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class RecipeRepository(
    context: Context,
) {
    private val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()
    private val remoteDataSource: RecipeRemoteDataSource = RecipeRemoteDataSource()
    private val spoonacularApi: SpoonacularApi = SpoonacularClient.create()

    val localRecipesFlow: Flow<List<Recipe>> = recipeDao.getAllRecipes()
        .map { list -> list.map { it.toRecipe() } }

    val localRecipesWithUserFlow: Flow<List<RecipeWithUser>> = localRecipesFlow
        .mapLatest { recipes ->
            coroutineScope {
                val recipeWithUserList = recipes.map { recipe ->
                    async {
                        val user = remoteDataSource.getCachedUser(recipe.senderId)
                            ?: remoteDataSource.getUserById(recipe.senderId)

                        RecipeWithUser(recipe, user)
                    }
                }

                recipeWithUserList.awaitAll()
            }
        }

    suspend fun refreshRecipesFromRemote() {
        try {
            val recipes = remoteDataSource.fetchRecipes().first()
            val entities = recipes.map { it.toEntity() }
            recipeDao.insertRecipes(entities)
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error refreshing recipes", e)
            throw e
        }
    }

    suspend fun getSpoonacularRecipes(count: Int = 10): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val apiKey = "165c6099f12842b1b3996b779d6fd6ef" // Better to store in a secure config
            val response = spoonacularApi.getRandomRecipes(count, apiKey)
            response.recipes.map { it.toAppRecipe() }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error fetching Spoonacular recipes", e)
            throw e
        }
    }

    fun wrapSpoonacularRecipes(recipes: List<Recipe>): List<RecipeWithUser> {
        val fakeUser = User(
            "Spoonacular",
            "Spoonacular",
            "Spoonacular",
            "https://play-lh.googleusercontent.com/uOZlIZUJ7R79qs_J_a9cdxrJaGhHwqKTmika25Lp1vTeC1qe9lPQF5jalEFc8Htk7nQ"
        )
        return recipes.map { recipe ->
            RecipeWithUser(recipe = recipe, user = fakeUser)
        }
    }

    suspend fun toggleLike(recipe: Recipe, userId: String) = withContext(Dispatchers.IO) {
        try {
            val updatedLikes = if (recipe.likes.contains(userId)) {
                recipe.likes - userId
            } else {
                recipe.likes + userId
            }
            val updatedRecipe = recipe.copy(likes = updatedLikes)

            // Update local database
            insertRecipe(updatedRecipe.toEntity())

            // Update remote database
            remoteDataSource.updateRecipeLikes(recipe.id, updatedLikes)
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error toggling like", e)
            throw e
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

    suspend fun getAllRecipes(): Flow<List<RecipeEntity>> {
        return withContext(Dispatchers.IO) {
            recipeDao.getAllRecipes()
        }
    }

    suspend fun deleteRecipesByUser(userId: String) {
        withContext(Dispatchers.IO) {
            recipeDao.deleteRecipesByUser(userId)
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

    suspend fun deleteRecipesById(recipeId: String, callback: (Boolean, Int) -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                val deletedCount = recipeDao.deleteRecipesById(recipeId)

                remoteDataSource.deleteRecipeById(
                    recipeId,
                    onSuccess = {
                        callback(true, deletedCount)
                    },
                    onFailure = { e ->
                        Log.e("RecipeRepository", "Error deleting recipe from Firestore", e)
                        callback(false, deletedCount)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error deleting recipe", e)
            callback(false, 0)
        }
    }
}