package com.github.epfl.meili.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PostViewModel(post_id: String) : ViewModel() {

    private val _post = MutableLiveData<Post?>() // makes it easier to manage
    val post: LiveData<Post?> = _post // public post to expose safely to the View (not mutable)

    init {
        viewModelScope.launch { // Asynchronous block for viewModels
            _post.value = PostService.getPostFromId(post_id)  // can be null
        }
    }
}