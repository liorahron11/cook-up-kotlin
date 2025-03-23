package com.example.cookup.room.dao

import androidx.room.*
import com.example.cookup.room.entities.Profile

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile)

    @Query("SELECT * FROM profile LIMIT 1")
    suspend fun getProfile(): Profile?

    @Update
    suspend fun updateProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)
}
