package com.github.epfl.meili.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.ChatMessage
import java.util.*

object ChatMessageViewModel : ViewModel(),
    Observer {

    lateinit var database: MessageDatabase

    private val _messages = MutableLiveData<List<ChatMessage>?>()
    val messages: LiveData<List<ChatMessage>?> = _messages

    init {
        database.addObserver(this)
    }

    fun addMessage(text: String, fromId: String, toId: String, timeStamp: Long) {
        val message = ChatMessage(
            text,
            fromId,
            toId,
            timeStamp
        )

        database.addMessageToDatabase(message)
    }

    override fun update(o: Observable?, arg: Any?) {
        _messages.value = database.messages
    }
}