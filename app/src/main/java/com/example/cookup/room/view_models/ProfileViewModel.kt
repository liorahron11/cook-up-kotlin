package com.example.cookup.room.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookup.room.entities.Profile
import com.example.cookup.room.repositories.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(application)

    fun insertProfile(profile: Profile) {
        viewModelScope.launch {
            repository.insertProfile(profile)
        }
    }

    fun getProfile(callback: (Profile?) -> Unit) {
        viewModelScope.launch {
            val profile = repository.getProfile()
            callback(profile)
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }
}
