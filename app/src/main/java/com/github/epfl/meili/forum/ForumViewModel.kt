package com.github.epfl.meili.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ForumViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>() // private list prevents exposing mutability
    val posts: LiveData<List<Post>> = _posts // public list to expose the posts to the View

    init {
        viewModelScope.launch { // Asynchronous block for viewModels
            _posts.value = PostService.getPosts()
        }
    }
}