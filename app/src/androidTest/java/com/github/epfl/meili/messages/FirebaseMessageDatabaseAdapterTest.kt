package com.github.epfl.meili.messages

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.models.ChatMessage
import com.google.firebase.database.DatabaseError
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class FirebaseMessageDatabaseAdapterTest {
    // Path to locate chat inside database
    private val MOCK_PATH = "POI/mock-poi"
    private val fake_message =
        ChatMessage("fake_text", "fake_from_id", "fake_to_id", 12345)

    private lateinit var db: FirebaseMessageDatabaseAdapter

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
        val expectedList = db.getMessages()
        expectedList.add(fake_message)

        val observer = Observer { _: Observable, _: Any ->
            assertEquals(expectedList, db.getMessages())
        }

        db.addObserver(observer)

        db.addMessageToDatabase(fake_message)
    }

    /*
    @Test
    fun onChildChangedTest(){
        // Do nothing
        db.onChildChanged(DataSnapshot. ("path"),null)
    }

    @Test
    fun onChildRemovedTest(){
        // Do nothing
        db.onChildRemoved(null)
    }

    @Test
    fun onChildMovedTest(){
        // Do nothing
        db.onChildRemoved(null)
    }
   */
    @Test
    fun onCancelledTest() {
        // Do nothing
        db.onCancelled(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION))
    }
}