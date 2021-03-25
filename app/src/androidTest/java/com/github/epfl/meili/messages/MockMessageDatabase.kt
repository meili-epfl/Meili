package com.github.epfl.meili.messages

import com.github.epfl.meili.models.ChatMessage
import java.util.*
import kotlin.collections.ArrayList

class MockMessageDatabase(path: String) : MessageDatabase(path) {
    private var messages: ArrayList<ChatMessage> = ArrayList()

    init {
        // Add mock messages
        val mock_message = ChatMessage("Hi I am a Mock Message", "Meili", "tour-eiffel", 1234)
        messages.add(mock_message)
        notifyObservers()
    }

    override fun addMessageToDatabase(chatMessage: ChatMessage) {
        messages.add(chatMessage)
        notifyObservers()
    }

    override fun getMessages(): ArrayList<ChatMessage> {
        return messages
    }
}