package com.example.cleanlabel.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthProfileDao {
    @Query("SELECT * FROM health_profiles")
    fun getAllProfiles(): Flow<List<HealthProfile>>

    @Query("SELECT * FROM health_profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): HealthProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: HealthProfile)

    @Update
    suspend fun updateProfile(profile: HealthProfile)

    @Delete
    suspend fun deleteProfile(profile: HealthProfile)

    @Query("DELETE FROM health_profiles")
    suspend fun deleteAllProfiles()
} 