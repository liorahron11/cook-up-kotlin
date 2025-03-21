package com.example.cookup.services

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.cookup.models.Recipe
import com.example.cookup.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun uploadProfileImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // Correctly returns image URL
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "שגיאה בהעלאת התמונה")
            }
    }

    fun saveUserProfile(
        email: String,
        username: String,
        profileImageUrl: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val userMap = hashMapOf(
            "uid" to userId,
            "email" to email,
            "username" to username,
            "profileImageUrl" to profileImageUrl
        )

        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "שגיאה בשמירת נתוני המשתמש")
            }
    }

    fun getUserProfile(onSuccess: (DocumentSnapshot) -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document -> onSuccess(document) }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "שגיאה בשליפת נתוני המשתמש")
            }
    }

    fun updateUserField(field: String, newValue: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val userRef = firestore.collection("users").document(userId)

        when (field.lowercase()) {
            "username" -> {
                userRef.update("username", newValue)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Error updating username") }
            }
            "email" -> {
                auth.currentUser?.updateEmail(newValue)
                    ?.addOnSuccessListener {
                        userRef.update("email", newValue)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onFailure(e.message ?: "Error updating email") }
                    }
                    ?.addOnFailureListener { e -> onFailure(e.message ?: "Error updating email") }

            }
            "password" -> {
                auth.currentUser?.updatePassword(newValue)
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener { e -> onFailure(e.message ?: "Error updating password") }
            }
            else -> {
                onFailure("Invalid field")
            }
        }
    }


    fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("recipes").add(recipe)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getUserRecipe(userId: String, onSuccess: (List<Recipe>) -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("recipes")
            .whereEqualTo("senderId", userId)
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.documents.mapNotNull { it.toObject(Recipe::class.java) }
                onSuccess(recipes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "שגיאה בשליפת מתכוני המשתמש")
            }
    }

        fun fetchFeedRecipes(): Flow<List<Recipe>> = callbackFlow {
            val listener = firestore.collection("recipes")
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
