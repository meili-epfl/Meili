package com.github.epfl.meili.messages

import androidx.lifecycle.LiveData
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
    abstract var messages: ArrayList<ChatMessage>
    private var observers: ArrayList<Observer> = ArrayList()

    init {
        if (path == "") {
            throw IllegalArgumentException("Path cannot be null")
        }
    }

    abstract fun addMessageToDatabase(chatMessage: ChatMessage)

    override fun addObserver(o: Observer?) { //TODO: write tests for observers
        super.addObserver(o)
        if(o != null && !observers.contains(o)){
            observers.add(o)
        }
        notifyObservers()
    }

    override fun notifyObservers() {
        super.notifyObservers()

        for(observer in observers){
            observer.update(this, messages)
        }
    }
}