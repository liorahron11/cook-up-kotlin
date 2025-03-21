package com.example.cookup.room

import com.example.cookup.models.Recipe
import com.example.cookup.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RecipeRemoteDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val userCache = mutableMapOf<String, User>()

    suspend fun getCachedUser(userId: String): User? {
        return userCache[userId] ?: run {
            val user = getUserById(userId)
            if (user != null) userCache[userId] = user
            user
        }
    }

    fun fetchRecipes(): Flow<List<Recipe>> = callbackFlow {
        val listener = db.collection("recipes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                val recipes = snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) }
                trySend(recipes)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
