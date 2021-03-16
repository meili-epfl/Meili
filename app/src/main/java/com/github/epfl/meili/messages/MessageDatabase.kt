package com.github.epfl.meili.messages

import com.github.epfl.meili.models.ChatMessage
import java.lang.IllegalArgumentException
import java.util.*

abstract class MessageDatabase(path: String): Observable() {
    init {
        if (path == ""){
            throw IllegalArgumentException("Path cannot be null")
        }
    }

    abstract fun addMessageToDatabase(path: String, chatMessage: ChatMessage)
}