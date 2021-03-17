package com.github.epfl.meili.forum

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MockPostService : PostService {

    val database = ArrayList<Post>()

    /** Get Post data from its id */
    override suspend fun getPostFromId(id: String?): Post? { // suspend makes function asynchronous
        if (id == null) {
            return null
        }

        return database.get(0)
    }

    /** Get multiple posts from Database */
    override suspend fun getPosts(): List<Post> {
        return database
    }

    /** Add new post to Database */
    override fun addPost(author: String, title: String, text: String) {
        database.add(Post("1", "2", "3", "4"))
    }
}