package com.github.epfl.meili.messages

import android.util.Log
import com.github.epfl.meili.models.ChatMessage
import com.github.epfl.meili.models.ChatMessage.Companion.toChatMessage
import com.github.epfl.meili.models.User
import com.github.epfl.meili.models.User.Companion.toUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp

// TOREMOVE when tests for ViewModel pass
object ChatMessageService {
    private const val TAG = "ChatMessageService"

    suspend fun getMessageWithToIdAndFromId(toId: String, fromId: String): List<ChatMessage>? { // suspend makes function asynchronous
        val db = Firebase.firestore
        return try {
            db.collection("messages")
                .whereEqualTo("toId", toId)
                .whereEqualTo("fromId", fromId)
                .get()
                .addOnSuccessListener { Log.d(TAG, "succes") }
                .addOnFailureListener {  Log.d(TAG, "failure")}
                .await() // wait asynchronously for data to arrive
                .documents.mapNotNull {
                    Log.d(TAG, "the message is ${it.toChatMessage()}")
                    it.toChatMessage() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Post from database")
            null // Return null if error occurs
        }
    }

     fun addMessage(text : String, fromId: String, toId: String, timestamp: Long){
        val db = Firebase.firestore

        val messageDocument = hashMapOf(
            "text" to text,
            "fromId" to fromId,
            "toId" to toId,
            "timestamp" to timestamp
        )

        db.collection("messages").add(messageDocument)
    }

}