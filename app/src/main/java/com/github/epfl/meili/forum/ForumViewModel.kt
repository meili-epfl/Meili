package com.github.epfl.meili.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.epfl.meili.messages.MessageDatabase
import kotlinx.coroutines.launch
import java.util.*

object ForumViewModel : ViewModelWithPostService(), Observer {

    private val _posts = MutableLiveData<List<Post>>() // private list prevents exposing mutability
    val posts: LiveData<List<Post>> = _posts // public list to expose the posts to the View

    fun changePostService(postService: PostService){
        this.postService = postService
        postService.addObserver(this)
    }
    /** Called when notified to update posts */
    override fun update(o: Observable?, arg: Any?) {
        syncPosts()
    }

    /** Synchronize posts */
    private fun syncPosts() {
        viewModelScope.launch { // Asynchronous block for live data
            _posts.value = postService.getPosts()
        }
    }
}