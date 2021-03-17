package com.github.epfl.meili.forum

import androidx.lifecycle.ViewModel

abstract class ViewModelWithPostService : ViewModel() {
    // Default is Firebase PostService (can be changed using setPostService)
    var postService: PostService = FirebasePostService
}