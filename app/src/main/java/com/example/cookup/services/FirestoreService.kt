package com.example.cookup.services

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.DocumentSnapshot

class FirestoreService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
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
}
