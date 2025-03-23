package com.example.cookup.room.dao

import androidx.room.*
import com.example.cookup.room.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipe(recipeId: String): RecipeEntity?

    @Query("DELETE FROM recipes WHERE senderId = :userId")
    suspend fun deleteRecipesByUser(userId: String)

    @Query("SELECT * FROM recipes WHERE senderId = :senderId")
    suspend fun getRecipesBySenderId(senderId: String): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes ORDER BY timestamp DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("DELETE FROM recipes")
    suspend fun clearAll()

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipesById(recipeId: String): Int
}
