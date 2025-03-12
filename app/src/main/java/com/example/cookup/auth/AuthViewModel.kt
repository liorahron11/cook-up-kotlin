package com.example.cookup.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPref = application.getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    private val _signupStatus = MutableLiveData<Boolean>()
    val signupStatus: LiveData<Boolean> = _signupStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        _loginStatus.value = isLoggedIn
    }

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isLoading.value = true  // Show loading spinner
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isLoading.value = false  // Hide loading spinner
                    if (task.isSuccessful) {
                        saveLoginState(true)
                        _loginStatus.value = true
                    } else {
                        _errorMessage.value = getHebrewErrorMessage(task.exception)
                    }
                }
        } else {
            _errorMessage.value = "יש למלא אימייל או סיסמה"
        }
    }

    fun signup(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isLoading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveLoginState(true)
                        _signupStatus.value = true
                    } else {
                        _errorMessage.value = getHebrewErrorMessage(task.exception)
                    }
                    _isLoading.value = false
                }
        } else {
            _errorMessage.value = "נדרש למלא את כל השדות"
        }
    }

    fun logout() {
        auth.signOut()
        saveLoginState(false)
        _loginStatus.value = false
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }
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
