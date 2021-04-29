package com.github.epfl.meili.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.models.User

class ProfileViewModelFactory(private val user: User): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProfileViewModel(user) as T
    }
}