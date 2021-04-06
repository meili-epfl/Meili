package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.forum.Post.Companion.toPost
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FirebasePostService() : PostService() {

    companion object {
        val DEFAULT_DB = { FirebaseFirestore.getInstance() }

        // Change this for dependency injection
        var dbProvider: () -> FirebaseFirestore = DEFAULT_DB
    }

    private val TAG = "FirebasePostService"
    private var db: FirebaseFirestore = dbProvider()

    init {
        // Listen to changes in post collection and make appropriate changes
        db.collection("posts").addSnapshotListener { snapshot, e ->
            // Handle errors
            if (e != null || snapshot == null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            // Modify local list for each change in Firestore database
            for (postDocument in snapshot!!.documentChanges) {
                val post = postDocument.document.toPost()!!
                when (postDocument.type) {
                    DocumentChange.Type.ADDED -> posts.add(post)
                    DocumentChange.Type.MODIFIED -> {
                        posts.remove(post)
                        posts.add(post)
                    }
                    DocumentChange.Type.REMOVED -> posts.remove(post)
                }
            }

            notifyObservers()
        }
    }

    /** Get Post data from its id */
    override fun getPostFromId(id: String?): Post? {
        for (p in posts) {
            if (p.id == id) {
                return p
            }
        }

        return null
    }

    /** Get all posts from Database */
    override fun getPosts(): List<Post> {
        return posts
    }

    /** Add new post to Database */
    override fun addPost(author: String, title: String, text: String) {
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
