package com.github.epfl.meili.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String,
    val username: String,
    val email: String,
    val bio: String,
    val profilePicturePath: String?
) : Parcelable {

    /** Creates an instances of User from other objects */
    companion object {
        private const val TAG = "User"

        // Create Post from Firestore data
        fun DocumentSnapshot.toUser(): User? {
            return try {
                val uid = getString("uid")!!
                val username = getString("username")!!
                val email = getString("email")!!
                val bio= getString("bio")!!
                val imageUrl= getString("profilePhoto")!!
                User(uid, username, email, bio, imageUrl)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to User")
                null // Return null if error occurs
            }
        }
    }
}
