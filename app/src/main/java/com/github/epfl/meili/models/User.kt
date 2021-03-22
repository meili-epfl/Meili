package com.github.epfl.meili.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String,
    val username: String,
) : Parcelable {

    /** Creates an instances of Post from other objects */
    companion object {
        private const val TAG = "User"

        // Create Post from Firestore data
        fun DocumentSnapshot.toUser(): User? {
            return try {
                val uid = getString("uid")!!
                val username = getString("username")!!
                User(uid, username)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to User")
                null // Return null if error occurs
            }
        }
    }
}