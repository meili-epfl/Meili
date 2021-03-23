package com.github.epfl.meili.messages

import android.util.Log
import com.github.epfl.meili.models.ChatMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class Adapter for the Firebase Database for chat messages.
 *
 * Remember that MessageDatabase class extends from observable so you can
 * add an observer to this class to listen to any changes in the Database
 * with the given path
 *
 * @param path: Path inside the firebase database to the chat group
 * (e.g. POI/tour-eiffel for the Tour Eiffel's POI chat)
 */
class FirebaseMessageDatabaseAdapter(private val path: String) : MessageDatabase(path),
    ChildEventListener {
    override var messages: ArrayList<ChatMessage> = ArrayList()

    private var databaseInstance: FirebaseDatabase = FirebaseDatabase.getInstance()

    init {
        val ref = databaseInstance.getReference(path)

        // Subscribe instance to the Database Reference
        ref.addChildEventListener(this)
    }

    /**
     * Add message the corresponding Firebase Database in path
     *
     * @param path: Path inside the firebase database to the chat group
     * @param chatMessage: chat message to be added inside the database
     */
    override fun addMessageToDatabase(chatMessage: ChatMessage) {
        val reference = databaseInstance.getReference(path).push()
        if(Firebase.auth.uid != null){
            chatMessage.fromId = Firebase.auth.uid!!
        }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG + path, "Saved our chat message: ${reference.key}")
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


    companion object {
        private const val TAG = "Database Message"
    }
}