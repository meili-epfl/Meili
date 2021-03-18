package com.github.epfl.meili.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.ChatMessage
import java.util.*

class ChatMessageViewModel(fromId: String, private val poiId: String) : ViewModel(), Observer {
    private val path = "POI/" + poiId
    private var database: FirebaseMessageDatabaseAdapter = FirebaseMessageDatabaseAdapter(path)

    // var chatMessageService: ChatMessageService = ChatMessageService
    private val _messages = MutableLiveData<List<ChatMessage>?>()
    val messages: LiveData<List<ChatMessage>?> = _messages

    init {
        database.addObserver(this)

        /*viewModelScope.launch { // Asynchronous block for viewModels
            _messages.value = chatMessageService.getMessageWithToIdAndFromId(fromId, toId) // can be null
        }*/
    }

    fun addMessage(text: String, fromId: String, toId: String, timeStamp: Long) {
        // chatMessageService.addMessage(text, fromId, toId, timeStamp)
        val message = ChatMessage(
            text,
            fromId,
            toId,
            timeStamp
        ) //todo: how to get message id? is the message id even needed??

        database.addMessageToDatabase(path, message)
    }

    override fun update(o: Observable?, arg: Any?) {
        _messages.value = database.messages
    }
}