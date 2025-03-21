package com.example.cookup.room.repositories

import android.content.Context
import com.example.cookup.interfaces.SpoonacularApi
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.models.toAppRecipe
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.databases.RecipeDatabase
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.utils.toEntity
import com.example.cookup.utils.toRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class RecipeRepository(context: Context, private val remoteDataSource: RecipeRemoteDataSource, private val spoonacularApi: SpoonacularApi) {
    private val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()

    val localRecipesFlow: Flow<List<Recipe>> = recipeDao.getAllRecipes()
        .map { list -> list.map { it.toRecipe() } }

    suspend fun refreshRecipesFromRemote() {
        val recipes = remoteDataSource.fetchRecipes().first()
        val entities = recipes.map { it.toEntity() }
        recipeDao.insertRecipes(entities)
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

}
