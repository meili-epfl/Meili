package com.github.epfl.meili.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.epfl.meili.models.ChatMessage
import com.github.epfl.meili.models.User
import kotlinx.coroutines.launch

class ChatMessageViewModel(fromId: String, toId: String): ViewModel() {
    var chatMessageService: ChatMessageService = ChatMessageService
    private val _messages = MutableLiveData<List<ChatMessage>?>()
    val messages: LiveData<List<ChatMessage>?> = _messages

    init {
        viewModelScope.launch { // Asynchronous block for viewModels
            _messages.value = chatMessageService.getMessageWithToIdAndFromId(fromId, toId) // can be null
        }
    }

    fun addMessage(text: String, fromId: String, toId: String, timeStamp: Long){
        chatMessageService.addMessage(text, fromId, toId, timeStamp)
    }

}