package com.github.epfl.meili.messages

import com.github.epfl.meili.models.ChatMessage
import java.util.*

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
    init {
        if (path == "") {
            throw IllegalArgumentException("Path cannot be null")
        }
    }

    abstract fun addMessageToDatabase(path: String, chatMessage: ChatMessage)
}