package com.example.cookup.room.repositories

import android.content.Context
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
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

class RecipeRepository(context: Context, private val remoteDataSource: RecipeRemoteDataSource) {
    private val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()

    val recipesFlow: Flow<List<Recipe>> = recipeDao.getAllRecipes()
        .map { it.map { entity -> entity.toRecipe() } }

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
