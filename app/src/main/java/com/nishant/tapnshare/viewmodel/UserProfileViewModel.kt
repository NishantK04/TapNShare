package com.nishant.tapnshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.tapnshare.data.AppDatabase
import com.nishant.tapnshare.data.UserProfile
import com.nishant.tapnshare.data.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserProfileRepository

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> get() = _profile

    init {
        val dao = AppDatabase.getDatabase(application).userProfileDao()
        repository = UserProfileRepository(dao)

        viewModelScope.launch {
            _profile.value = repository.getProfile()
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            _profile.value = profile
        }
    }

    fun clearProfile() {
        viewModelScope.launch {
            repository.clearProfile()
            _profile.value = null
        }
    }
}
