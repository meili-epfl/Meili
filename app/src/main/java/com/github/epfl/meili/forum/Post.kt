package com.github.epfl.meili.forum

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val id: String,
    val author: String,
    val title: String,
    val text: String
) : Parcelable {

    /** Creates an instances of Post from other objects */
    companion object {
        private const val TAG = "Post"

        // Create Post from Firestore data
        fun DocumentSnapshot.toPost(): Post? {
            return try {
                val id = id
                val author = getString("username")!! // !! Means we guarantee not null
                val title = getString("title")!!
                val text = getString("text")!!
                Post(id, author, title, text)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to Post")
                null // Return null if error occurs
            }
        }
    }


}