package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.home.Auth

object NewPostViewModel : ViewModelWithPostService() {

    private val TAG = "NewPostViewModel"

    /** Create new post in database for this particular user */
    fun createNewPost(title: String, text: String) {
        if (Auth.name != null) {
            postService.addPost(Auth.name!!, title, text)
        } else {
            Log.e(TAG, "Error: non-logged in user is trying to create post")
        }
    }
}