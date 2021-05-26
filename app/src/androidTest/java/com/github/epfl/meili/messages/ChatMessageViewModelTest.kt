package com.github.epfl.meili.messages

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.models.ChatMessage
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatMessageViewModelTest {
    private var mockPath = "POI/tour-eiffel"
    private var mockMessage1 = ChatMessage("Hi I am a Mock Message", "Meili", "tour-eiffel", 1234)
    private lateinit var db: MessageDatabase

    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            db = MockMessageDatabase(mockPath)
            ChatMessageViewModel.setMessageDatabase(db)
        }
    }

    @Test
    fun addMessageTest() {
        UiThreadStatement.runOnUiThread {
            val mockMessage2 = ChatMessage("hi", "me", "you", 1234, "MyName")

            ChatMessageViewModel.addMessage(
                    mockMessage2.text,
                    mockMessage2.fromId,
                    mockMessage2.toId,
                    mockMessage2.timestamp,
                    mockMessage2.fromName
            )

            val expectedMessageList = ArrayList<ChatMessage>()
            expectedMessageList.add(mockMessage1)
            expectedMessageList.add(mockMessage2)

            assertEquals(db.getMessages(), expectedMessageList)
        }
    }

    @Test
    fun viewModelIsObserving() {
        UiThreadStatement.runOnUiThread {
            val expectedMessageList = ArrayList<ChatMessage>()
            expectedMessageList.add(mockMessage1)

            assertEquals(expectedMessageList, ChatMessageViewModel.messages.value)

            val mockMessage2 = ChatMessage("hi", "me", "you", 1234)
            db.addMessageToDatabase(mockMessage2)

            expectedMessageList.add(mockMessage2)

            assertEquals(expectedMessageList, db.getMessages())
            assertEquals(expectedMessageList, ChatMessageViewModel.messages.value)
        }
    }
}