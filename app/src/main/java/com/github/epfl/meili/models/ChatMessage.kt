package com.github.epfl.meili.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timestamp: Long
) : Parcelable {
    /** Creates an instances of Post from other objects */
    companion object {
        private const val TAG = "ChatMessage"

        // Create Post from Firestore data
        fun DocumentSnapshot.toChatMessage(): ChatMessage? {
            return try {
                val id =  id
                val text = getString("text")!!
                val fromId = getString("fromId")!!
                val toId = getString("toId")!!
                val timestamp = getLong("timestamp")!!
                ChatMessage(id, text, fromId, toId, timestamp)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to Chat Message")
                null // Return null if error occurs
            }
        }
    }
}