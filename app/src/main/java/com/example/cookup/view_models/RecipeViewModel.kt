package com.example.cookup.view_models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.cookup.models.Recipe
import com.example.cookup.services.FirestoreService
import com.google.firebase.storage.FirebaseStorage

class RecipeViewModel : ViewModel() {
    private val firestoreService = FirestoreService()
    private val storage = FirebaseStorage.getInstance().reference

    fun uploadImage(imageUri: Uri, recipeId: String, callback: (String?) -> Unit) {
        val fileRef = storage.child("recipe_images/${recipeId}")
        fileRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                fileRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result.toString())
                } else {
                    callback(null)
                }
            }
    }

    fun addRecipe(recipe: Recipe, callback: (Boolean) -> Unit) {
        firestoreService.addRecipe(recipe,
            onSuccess = {
                Log.d("RecipeViewModel", "Recipe added successfully")
                callback(true)
            },
            onFailure = { e ->
                Log.e("RecipeViewModel", "Error adding recipe", e)
                callback(false)
            }
        )
    }
}
