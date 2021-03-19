package com.github.epfl.meili.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.epfl.meili.models.User
import kotlinx.coroutines.launch

class UserListViewModel() : ViewModel() {
    var userFetchingService: UserFetchingService = UserFetchingService
    private val _users = MutableLiveData<List<User>?>()
    val users: LiveData<List<User>?> = _users

    init {
        viewModelScope.launch { // Asynchronous block for viewModels
            _users.value = userFetchingService.getUsers() // can be null
        }
    }
}