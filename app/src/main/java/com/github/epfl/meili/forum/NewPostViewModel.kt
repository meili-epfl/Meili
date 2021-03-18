package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.home.Auth
import java.util.*

object NewPostViewModel : ViewModelWithPostService() {

    private val TAG = "NewPostViewModel"

    override fun update(o: Observable?, arg: Any?) {
        // Do nothing
    }

    /** Create new post in database for this particular user */
    fun createNewPost(title: String, text: String) {
        if (Auth.name != null) {
            postService.addPost(Auth.name!!, title, text)
        } else {
            Log.e(TAG, "Error: non-logged in user is trying to create post")
        }
    }
}