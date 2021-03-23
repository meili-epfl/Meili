package com.github.epfl.meili.forum

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

/** Abstract class for ViewModels using a Database of Posts */
abstract class ViewModelWithPostService : ViewModel(), Observer {

    lateinit var postService: FirebasePostService

    init {
        setService(FirebaseFirestore.getInstance()) // Default is normal Firestore
    }

    fun setService(new: FirebaseFirestore) {
        postService = FirebasePostService(new)
        postService.addObserver(this) // Observe changes from the service
    }
}