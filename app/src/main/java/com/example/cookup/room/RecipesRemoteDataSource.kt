package com.example.cookup.room

import com.example.cookup.models.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RecipeRemoteDataSource {
    private val db = FirebaseFirestore.getInstance()

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
}
