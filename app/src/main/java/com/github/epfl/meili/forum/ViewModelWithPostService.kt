package com.github.epfl.meili.forum

import androidx.lifecycle.ViewModel

/** Abstract class for ViewModels using a Database of Posts */
abstract class ViewModelWithPostService : ViewModel() {
    // Default is Firebase PostService (can be changed for tests)
    var postService: PostService = FirebasePostService()
}