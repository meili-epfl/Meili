package com.github.epfl.meili.messages

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.models.ChatMessage
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class FirebaseMessageDatabaseAdapterTest {
    private val MOCK_PATH = "POI/mock-poi"
    private lateinit var db: FirebaseMessageDatabaseAdapter
    private val fake_message =
        ChatMessage("fake_id", "fake_text", "fake_from_id", "fake_to_id", 12345)

    @Before
    fun initializeDatabase() {
        UiThreadStatement.runOnUiThread {
            db = FirebaseMessageDatabaseAdapter(MOCK_PATH)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructorThrowsWhenNullArgs() {

        FirebaseMessageDatabaseAdapter("")

    }

    @Test
    fun adddMessageTest() {
        val expectedList = db.messages
        expectedList.add(fake_message)

        val observer = Observer { _: Observable, _: Any ->
            assertEquals(expectedList, db.messages)
        }

        db.addObserver(observer)

        db.addMessageToDatabase(MOCK_PATH, fake_message)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addMessageToDatabaseThrowsWhenNullArgs() {

        db.addMessageToDatabase("", fake_message)

    }
}