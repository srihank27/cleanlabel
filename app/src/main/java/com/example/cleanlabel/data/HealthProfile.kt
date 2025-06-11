package com.example.cleanlabel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "health_profiles")
@TypeConverters(Converters::class)
data class HealthProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val conditions: List<String>
) 