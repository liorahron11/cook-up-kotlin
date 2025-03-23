package com.example.cookup.room.repositories

import android.content.Context
import com.example.cookup.room.databases.ProfileDatabase
import com.example.cookup.room.entities.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(context: Context) {
    private val profileDao = ProfileDatabase.getDatabase(context).profileDao()

    suspend fun insertProfile(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDao.insertProfile(profile)
        }
    }

    suspend fun getProfile(): Profile? {
        return withContext(Dispatchers.IO) {
            profileDao.getProfile()
        }
    }

    suspend fun updateProfile(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDao.updateProfile(profile)
        }
    }

    suspend fun deleteProfile(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDao.deleteProfile(profile)
        }
    }
}
