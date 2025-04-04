package com.example.cookup.room

import com.example.cookup.models.Recipe
import com.example.cookup.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeRemoteDataSource {
    private val RECIPES_COLLECTION = "recipes"
    private val USERS_COLLECTION = "users"
    private val db = FirebaseFirestore.getInstance()
    private val userCache = mutableMapOf<String, User>()


    suspend fun getCachedUser(userId: String): User? {
        return userCache[userId] ?: run {
            val user = getUserById(userId)
            if (user != null) userCache[userId] = user
            user
        }
    }

    suspend fun fetchRecipes(): List<Recipe>? {
        return try {
            val snapshot = db.collection(RECIPES_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




    suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateRecipeLikes(recipeId: String, likes: List<String>) {
        val recipeCollection = FirebaseFirestore.getInstance().collection(RECIPES_COLLECTION)

        recipeCollection.document(recipeId)
            .update("likes", likes)
            .await()
    }

    fun deleteRecipeById(recipeId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(RECIPES_COLLECTION)
            .document(recipeId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
