package com.github.epfl.meili.messages

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.models.ChatMessage
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatMessageViewModelTest {
    var MOCK_PATH = "POI/tour-eiffel"
    var mock_message1 = ChatMessage("Hi I am a Mock Message", "Meili", "tour-eiffel", 1234)
    lateinit var db: MessageDatabase

    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            db = MockMessageDatabase(MOCK_PATH)
            ChatMessageViewModel.setMessageDatabase(db)
        }
    }

    @Test
    fun addMessageTest() {
        UiThreadStatement.runOnUiThread {
            val mock_message2 = ChatMessage("hi", "me", "you", 1234, "MyName")

            ChatMessageViewModel.addMessage(
                    mock_message2.text,
                    mock_message2.fromId,
                    mock_message2.toId,
                    mock_message2.timestamp,
                    mock_message2.fromName
            )

            val expectedMessageList = ArrayList<ChatMessage>()
            expectedMessageList.add(mock_message1)
            expectedMessageList.add(mock_message2)

            assertEquals(db.getMessages(), expectedMessageList)
        }
    }

    @Test
    fun viewModelIsObserving() {
        UiThreadStatement.runOnUiThread {
            var expectedMessageList = ArrayList<ChatMessage>()
            expectedMessageList.add(mock_message1)

            assertEquals(expectedMessageList, ChatMessageViewModel.messages.value)

            var mock_message2 = ChatMessage("hi", "me", "you", 1234)
            db.addMessageToDatabase(mock_message2)

            expectedMessageList.add(mock_message2)

            assertEquals(expectedMessageList, db.getMessages())
            assertEquals(expectedMessageList, ChatMessageViewModel.messages.value)
        }
    }
}