package com.github.epfl.meili.models

import android.os.Parcel
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test


class ChatMessageUnitTest {
    private val TEST_TEXT: String = "Hi!"
    private val TEST_FROMID: String = "frkhjf243htiu43iut"
    private val TEST_TOID: String = "fuh43koh31pjh1t4"
    private val TEST_TIMESTAMP: Long = 1L
    private val TEST_FROM_NAME: String = "testName"


    @Test
    fun chatMessageConstructor() {
        var chatMessage =
            ChatMessage(TEST_TEXT, TEST_FROMID, TEST_TOID, TEST_TIMESTAMP, TEST_FROM_NAME)
        assertThat(chatMessage.text, `is`(TEST_TEXT))
        assertThat(chatMessage.component1(), `is`(TEST_TEXT))
        assertThat(chatMessage.fromId, `is`(TEST_FROMID))
        assertThat(chatMessage.component2(), `is`(TEST_FROMID))
        assertThat(chatMessage.toId, `is`(TEST_TOID))
        assertThat(chatMessage.component3(), `is`(TEST_TOID))
        assertThat(chatMessage.timestamp, `is`(TEST_TIMESTAMP))
        assertThat(chatMessage.component4(), `is`(TEST_TIMESTAMP))
        assertThat(chatMessage.fromName, `is`(TEST_FROM_NAME))
        assertThat(chatMessage.component5(), `is`(TEST_FROM_NAME))
        assertThat(
            chatMessage.toString(),
            `is`("ChatMessage(text=Hi!, fromId=frkhjf243htiu43iut, toId=fuh43koh31pjh1t4, timestamp=1, fromName=testName)")
        )
        assertThat(
            chatMessage.hashCode(),
            `is`(
                ChatMessage(
                    TEST_TEXT,
                    TEST_FROMID,
                    TEST_TOID,
                    TEST_TIMESTAMP,
                    TEST_FROM_NAME
                ).hashCode()
            )
        )

        chatMessage.copy(TEST_TEXT, TEST_FROMID, TEST_TOID, TEST_TIMESTAMP, TEST_FROM_NAME)
        chatMessage.writeToParcel(Parcel.obtain(), 0)

    }


}