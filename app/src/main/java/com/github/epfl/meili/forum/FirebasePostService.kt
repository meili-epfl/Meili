package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.forum.Post.Companion.toPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebasePostService() : Observable() {

    companion object {
        val DEFAULT_DB = { FirebaseFirestore.getInstance() }

        // Change this for dependency injection
        var dbProvider: () -> FirebaseFirestore = DEFAULT_DB
    }

    private val TAG = "FirebasePostService"
    private var db: FirebaseFirestore = dbProvider()

    init {
        // Listen to changes in post collection and notify observers when it has changed
        db.collection("posts").addSnapshotListener { snapshot, e ->
            // Handle errors
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            setChanged() // Tell Observable that its state has changed
            notifyObservers()
        }
    }

    /** Get Post data from its id */
    suspend fun getPostFromId(id: String?): Post? { // suspend makes function asynchronous
        if (id == null) {
            return null
        }

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