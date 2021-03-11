package com.github.epfl.meili

import com.github.epfl.meili.models.ChatMessage
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ChatMessageUnitTest {
    private val TEST_ID: String = "1"
    private val TEST_TEXT: String = "Hi!"
    private val TEST_FROMID: String = "frkhjf243htiu43iut"
    private val TEST_TOID: String = "fuh43koh31pjh1t4"
    private val TEST_TIMESTAMP: Long = 1L


    @Test
    fun chatMessageConstructor() {
        var chatMessage = ChatMessage(TEST_ID, TEST_TEXT, TEST_FROMID, TEST_TOID, TEST_TIMESTAMP)
        assertThat(chatMessage.id, `is`(TEST_ID))
        assertThat(chatMessage.text, `is`(TEST_TEXT))
        assertThat(chatMessage.fromId, `is`(TEST_FROMID))
        assertThat(chatMessage.toId, `is`(TEST_TOID))
        assertThat(chatMessage.timestamp, `is`(TEST_TIMESTAMP))
    }

    @Test
    fun chatMessageDefaultConstructor(){
        var chatMessage = ChatMessage()
        assertThat(chatMessage.id, `is`(""))
        assertThat(chatMessage.text, `is`(""))
        assertThat(chatMessage.fromId, `is`(""))
        assertThat(chatMessage.toId, `is`(""))
        assertThat(chatMessage.timestamp, `is`(-1))
    }
}