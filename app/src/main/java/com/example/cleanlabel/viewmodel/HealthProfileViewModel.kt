package com.example.cleanlabel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanlabel.data.AppDatabase
import com.example.cleanlabel.data.HealthProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val healthProfileDao = database.healthProfileDao()

    private val _profiles = MutableStateFlow<List<HealthProfile>>(emptyList())
    val profiles: StateFlow<List<HealthProfile>> = _profiles.asStateFlow()

    private val _currentProfile = MutableStateFlow<HealthProfile?>(null)
    val currentProfile: StateFlow<HealthProfile?> = _currentProfile.asStateFlow()

    init {
        viewModelScope.launch {
            healthProfileDao.getAllProfiles().collect { profiles ->
                _profiles.value = profiles
                if (_currentProfile.value == null && profiles.isNotEmpty()) {
                    _currentProfile.value = profiles.first()
                } else if (_currentProfile.value != null) {
                    // Update current profile with latest data
                    val updatedProfile = profiles.find { it.id == _currentProfile.value?.id }
                    if (updatedProfile != null) {
                        _currentProfile.value = updatedProfile
                    }
                }
            }
        }
    }

    fun addProfile(name: String, age: Int, conditions: List<String>) {
        viewModelScope.launch {
            val profile = HealthProfile(
                name = name,
                age = age,
                conditions = conditions
            )
            healthProfileDao.insertProfile(profile)
            if (_currentProfile.value == null) {
                _currentProfile.value = profile
            }
        }
    }

    fun updateProfile(profile: HealthProfile) {
        viewModelScope.launch {
            healthProfileDao.updateProfile(profile)
            // Force update the profiles list to trigger recomposition
            val updatedProfiles = _profiles.value.toMutableList()
            val index = updatedProfiles.indexOfFirst { it.id == profile.id }
            if (index != -1) {
                updatedProfiles[index] = profile
                _profiles.value = updatedProfiles
            }
            // Update current profile if it's the one being updated
            if (_currentProfile.value?.id == profile.id) {
                _currentProfile.value = profile
            }
        }
    }

    fun deleteProfile(profile: HealthProfile) {
        viewModelScope.launch {
            healthProfileDao.deleteProfile(profile)
            if (_currentProfile.value?.id == profile.id) {
                _currentProfile.value = null
                // If there are other profiles, set the first one as current
                if (_profiles.value.isNotEmpty()) {
                    _currentProfile.value = _profiles.value.first()
                }
            }
        }
    }

    fun setCurrentProfile(profile: HealthProfile) {
        viewModelScope.launch {
            _currentProfile.value = profile
        }
    }

    fun updateCurrentProfile(
        name: String? = null,
        age: Int? = null,
        conditions: List<String>? = null
    ) {
        viewModelScope.launch {
            val currentProfile = _currentProfile.value ?: return@launch
            val updatedProfile = currentProfile.copy(
                name = name ?: currentProfile.name,
                age = age ?: currentProfile.age,
                conditions = conditions ?: currentProfile.conditions
            )
            healthProfileDao.updateProfile(updatedProfile)
            _currentProfile.value = updatedProfile
            // Force update the profiles list
            val updatedProfiles = _profiles.value.toMutableList()
            val index = updatedProfiles.indexOfFirst { it.id == updatedProfile.id }
            if (index != -1) {
                updatedProfiles[index] = updatedProfile
                _profiles.value = updatedProfiles
            }
        }
    }
} 