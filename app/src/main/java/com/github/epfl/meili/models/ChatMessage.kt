package com.github.epfl.meili.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val text: String = "",
    var fromId: String = "",
    val toId: String = "",
    val timestamp: Long = -1,
    val fromName: String = ""
) : Parcelable {
    /** Creates an instances of Post from other objects */
    companion object {
        private const val TAG = "ChatMessage"

        // Create Post from Firestore data
        fun DocumentSnapshot.toChatMessage(): ChatMessage? {
            return try {
                val text = getString("text")!!
                val fromId = getString("fromId")!!
                val toId = getString("toId")!!
                val timestamp = getLong("timestamp")!!
                val fromName = getString("fromName")!!
                ChatMessage(text, fromId, toId, timestamp, fromName)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to Chat Message")
                null // Return null if error occurs
            }
        }
    }
}
