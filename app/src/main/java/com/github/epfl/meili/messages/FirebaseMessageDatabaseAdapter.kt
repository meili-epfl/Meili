package com.github.epfl.meili.messages

import android.util.Log
import com.github.epfl.meili.ChatLogActivity
import com.github.epfl.meili.models.ChatMessage
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class FirebaseMessageDatabaseAdapter(path: String) : MessageDatabase(path), ChildEventListener {
    val messages = ArrayList<ChatMessage>()

    private var databaseInstance: FirebaseDatabase = FirebaseDatabase.getInstance()

    init {
        val ref = databaseInstance.getReference(path)
        ref.addChildEventListener(this)

    }

    override fun addMessageToDatabase(path: String, chatMessage: ChatMessage) {
        if (path == "") {
            throw IllegalArgumentException("Error: path cannot be empty")
        }

        val reference = databaseInstance.getReference(path).push()

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
            }
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val chatMessage = snapshot.getValue(ChatMessage::class.java)

        if (chatMessage != null) {
            messages.add(chatMessage)
            this.notifyObservers()
        }
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
    }

    override fun onCancelled(error: DatabaseError) {
    }
}