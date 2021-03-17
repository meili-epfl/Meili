package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.forum.Post.Companion.toPost
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object PostService {

    private const val TAG = "PostService"

    /** Get Post data from its id */
    suspend fun getPostFromId(id: String?): Post? { // suspend makes function asynchronous
        if (id == null) {
            return null
        }

        val db = Firebase.firestore
        return try {
            db.collection("posts")
                .document(id)
                .get()
                .await() // wait asynchronously for data to arrive
                .toPost() // Convert to Post instance
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Post from database")
            null // Return null if error occurs
        }
    }

    /** Get multiple posts from Database */
    suspend fun getPosts(): List<Post> {
        val db = Firebase.firestore
        return try {
            db.collection("posts")
                .get()
                .await() // wait asynchronously for data to arrive
                .documents.mapNotNull { it.toPost() } // Convert all conforming Posts to Post and add to list
        } catch (e: Exception) {
            Log.e(TAG, "Error getting multiple posts from database")
            emptyList<Post>() // Return empty list if error occurs
        }
    }

    /** Add new post to Database */
    fun addPost(author: String, title: String, text: String) {
        val db = Firebase.firestore

        // Create post document (ID created by database)
        val postDocument = hashMapOf(
            "username" to author,
            "title" to title,
            "text" to text
        )

        // Add a new document with a generated ID
        db.collection("posts").add(postDocument)
    }

}