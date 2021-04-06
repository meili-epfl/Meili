package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.home.Auth
import java.util.*

object NewPostViewModel : PostServiceViewModel() {

    private val TAG = "NewPostViewModel"


    override fun update(o: Observable?, arg: Any?) {
        // Do nothing
    }
    fun changePostService(postService: PostService){
        this.postService = postService
        postService.addObserver(this)
    }

    /** Create new post in database for this particular user */
    fun createNewPost(title: String, text: String) {
        if(postService is MockPostService){
            postService.addPost("mockTester", title, text)
            return
        }
        if (Auth.name != null) {
            postService.addPost(Auth.name!!, title, text)
        } else {
            Log.e(TAG, "Error: non-logged in user is trying to create post")
        }
    }
}