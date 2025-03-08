package com.example.cookup.auth

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cookup.models.User
import com.example.cookup.services.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestoreService = FirestoreService()
    private val sharedPref = application.getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    private val _signupStatus = MutableLiveData<Boolean>()
    val signupStatus: LiveData<Boolean> = _signupStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() {
        val uid = sharedPref.getString("uid", "").toString()
        val email = sharedPref.getString("email", "").toString()
        val username = sharedPref.getString("username", "").toString()
        val profileImageUrl = sharedPref.getString("profileImageUrl", "").toString()

        if (uid.isNotEmpty()) {
            _user.value = User(uid, email, username, profileImageUrl)
        }

        _loginStatus.value = _user.value != null
    }

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isLoading.value = true  // Show loading spinner
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isLoading.value = false  // Hide loading spinner
                    if (task.isSuccessful) {
                        firestoreService.getUserProfile(
                            onSuccess = { document ->
                                saveUserLocally(document.getString("uid").toString(),
                                    document.getString("email").toString(),
                                    document.getString("username").toString(),
                                    document.getString("profileImageUrl").toString())
                                _loginStatus.value = true
                            },
                            onFailure = { error ->
                                _errorMessage.value = error.toString()
                            }
                        )
                    } else {
                        _errorMessage.value = getHebrewErrorMessage(task.exception)
                    }
                }
        } else {
            _errorMessage.value = "יש למלא אימייל או סיסמה"
        }
    }

    fun signup(email: String, username: String, password: String, profileImageUri: Uri?) {
        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
            _isLoading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        profileImageUri?.let { uri ->
                            firestoreService.uploadProfileImage(uri,
                                onSuccess = { imageUrl ->
                                    saveUserProfile(user?.uid ?: "", user?.email ?: "", username, imageUrl)
                                },
                                onFailure = { error ->
                                    _isLoading.value = false
                                    _errorMessage.value = error
                                }
                            )
                        } ?: saveUserProfile(user?.uid ?: "",user?.email ?: "", username, null)
                    } else {
                        _errorMessage.value = getHebrewErrorMessage(task.exception)
                    }
                }
        } else {
            _errorMessage.value = "נדרש למלא את כל השדות"
        }
    }

    private fun saveUserProfile(uid: String, email: String, username: String, profileImageUrl: String?) {
        firestoreService.saveUserProfile(email, username, profileImageUrl,
            onSuccess = {
                _isLoading.value = false
                _signupStatus.value = true
                saveUserLocally(uid, email, username, profileImageUrl)
            },
            onFailure = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun logout() {
        auth.signOut()
        clearUserLocally()
        _user.value = null
        _loginStatus.value = false
    }

    private fun saveUserLocally(uid: String, email: String, username: String, profileImageUrl: String?) {
        with(sharedPref.edit()) {
            putString("uid", uid)
            putString("email", email)
            putString("username", username)
            putString("profileImageUrl", profileImageUrl)
            apply()
        }

        _user.value = User(uid, email, username, profileImageUrl)
    }

    private fun clearUserLocally() {
        with(sharedPref.edit()) {
            remove("uid")
            remove("email")
            remove("username")
            remove("profileImageUrl")
            apply()
        }

        _user.value = null
    }

    fun setAuthErrorMessage(message: String) {
        _errorMessage.value = message
    }

    private fun getHebrewErrorMessage(exception: Exception?): String {
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "האימייל אינו תקין"
            "ERROR_WRONG_PASSWORD" -> "הסיסמה שגויה"
            "ERROR_USER_NOT_FOUND" -> "המשתמש לא נמצא"
            "ERROR_USER_DISABLED" -> "המשתמש הזה הושבת"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "האימייל כבר בשימוש"
            "ERROR_WEAK_PASSWORD" -> "הסיסמה חלשה מדי"
            "ERROR_TOO_MANY_REQUESTS" -> "יותר מדי ניסיונות התחברות, נסה שוב מאוחר יותר"
            "ERROR_INVALID_CREDENTIAL" -> "אימייל או סיסמה אינם נכונים"
            else -> "אירעה שגיאה, נסה שוב"
        }
    }
}
