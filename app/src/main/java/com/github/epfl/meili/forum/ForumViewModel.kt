package com.github.epfl.meili.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

object ForumViewModel : ViewModelWithPostService(), Observer {

    private val _posts = MutableLiveData<List<Post>>() // private list prevents exposing mutability
    val posts: LiveData<List<Post>> = _posts // public list to expose the posts to the View

    init {
        postService.addObserver(this) // Observe changes from the service
    }

    /** Called when notified to update posts */
    override fun update(o: Observable?, arg: Any?) {
        syncPosts()
    }

    /** Synchronize posts */
    private fun syncPosts() {
        viewModelScope.launch { // Asynchronous block for viewModels
            _posts.value = postService.getPosts()
        }
    }
}