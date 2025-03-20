package com.example.cookup.room.dao

import androidx.room.*
import com.example.cookup.room.entities.RecipeEntity

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipe(recipeId: String): RecipeEntity?

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE senderId = :senderId")
    suspend fun getRecipesBySenderId(senderId: String): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)
}
