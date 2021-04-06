package com.github.epfl.meili.forum

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

/** Abstract class for ViewModels using a Database of Posts */
abstract class ViewModelWithPostService : ViewModel(), Observer {

    var postService: PostService = FirebasePostService()

    init {
        postService.addObserver(this) // Observe changes from the service
    }
}