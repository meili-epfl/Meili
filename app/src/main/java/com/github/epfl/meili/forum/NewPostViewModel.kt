package com.github.epfl.meili.forum

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.home.AuthenticationService

class NewPostViewModel : ViewModel() {

    private val TAG = "NewPostViewModel"

    // Create new post in database for this particular user
    fun createNewPost(title: String, text: String) {
        val currentUser = AuthenticationService.getCurrentUser()

        if (currentUser != null) {
            PostService.addPost(currentUser.displayName, title, text)
        } else {
            Log.e(TAG, "Error: non-logged in user is trying to create post")
        }
    }

}