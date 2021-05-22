package com.github.epfl.meili.messages

import com.github.epfl.meili.models.ChatMessage
import java.util.*
import kotlin.collections.ArrayList

/**
 * Abstract class of Databases for chat messages.
 *
 * MessageDatabase extends from observable so you can
 * add an observer to this class to listen to any changes in the Database
 * with the given path
 *
 * @param path: Path to locate chat inside database
 */
abstract class MessageDatabase(path: String) : Observable() {
    private var observers: ArrayList<Observer> = ArrayList()

    init {
        if (path.isEmpty()) {
            throw IllegalArgumentException("Path cannot be empty")
        }
    }

    /**
     * Add message to the database
     *
     * @param chatMessage: chat message to be added inside the database
     */
    abstract fun addMessageToDatabase(chatMessage: ChatMessage)

    /**
     * Get all messages from the database
     */
    abstract fun getMessages(): ArrayList<ChatMessage>

    override fun addObserver(o: Observer?) {
        super.addObserver(o)
        if(o != null && !observers.contains(o)){
            observers.add(o)
        }
        notifyObservers()
    }

    override fun notifyObservers() {
        super.notifyObservers()

        for(observer in observers){
            observer.update(this, getMessages())
        }
    }
}