package com.github.epfl.meili.forum

import androidx.lifecycle.ViewModel
import java.util.*

/** Abstract class for ViewModels using a Database of Posts */
abstract class ViewModelWithPostService : ViewModel(), Observer {

    lateinit var postService: PostService

    init {
        setService(FirebasePostService()) // Default is Firebase PostService (can be changed for tests)
    }

    fun setService(new: PostService) {
        postService = new
        postService.addObserver(this) // Observe changes from the service
    }
}