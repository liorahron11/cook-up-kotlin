package com.example.cookup.ui.login

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun onEmailChange(newEmail: String) {
        if (!newEmail.contains("\n")) {
            email = newEmail
        }
    }

    fun onPasswordChange(newPassword: String) {
        if (!newPassword.contains("\n")) {
            password = newPassword
        }
    }
}
