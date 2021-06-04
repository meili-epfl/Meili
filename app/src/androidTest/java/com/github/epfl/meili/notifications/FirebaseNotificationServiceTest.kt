package com.github.epfl.meili.notifications


import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.auth.AuthenticationService
import com.github.epfl.meili.messages.ChatActivity
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.PointOfInterest

import com.github.epfl.meili.models.User
import com.google.firebase.messaging.RemoteMessage
import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class FirebaseNotificationServiceTest {

    private val test_token: String = "4"

    private val mockPath = "POI/mock-poi"
    private val fakeMessage = "fake_text"
    private val fakeId = "fake_id"
    private val fakeName = "fake_name_sender"
    private val fakePoi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "fake_poi", "fake_poi")


    lateinit var instrumentationContext: Context


    private fun getIntent(): Intent {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = Intent(targetContext, ChatActivity::class.java).apply {
            putExtra("POI_KEY", fakePoi)
        }

        UiThreadStatement.runOnUiThread {
            val mockAuth = Mockito.mock(AuthenticationService::class.java)

            Mockito.`when`(mockAuth.getCurrentUser())
                .thenReturn(User("fake_uid", "fake_name", "fake_email", " "))

            Mockito.`when`(mockAuth.signInIntent(ArgumentMatchers.any())).thenReturn(intent)
            Auth.setAuthenticationService(mockAuth)
        }

        return intent
    }

    private val intent = getIntent()

    @get:Rule
    var mActivityTestRule: ActivityScenarioRule<ChatActivity> = ActivityScenarioRule(intent)


    @Before
    fun setupContext() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(mockPath))
            ChatMessageViewModel.addMessage(fakeMessage, fakeId, fakeId, 10)
        }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }
    @Before
    fun initMock(){
        UiThreadStatement.runOnUiThread{
            val mockAuth = Mockito.mock(AuthenticationService::class.java)

            Mockito.`when`(mockAuth.getCurrentUser())
                .thenReturn(User("fake_uid", "fake_name", "fake_email", " "))

            Auth.setAuthenticationService(mockAuth)
        }
    }

    @Test
    fun onNewTokenUpdatesToken(){
        val firebaseNotifService = FirebaseNotificationService()
        firebaseNotifService.onNewToken(test_token)
    }
    @Test
    fun messageReceivedCorrectly(){
        val firebaseNotifService = FirebaseNotificationService()
        val bundle = bundleOf(
            "title" to "test_title",
            "message" to "test_message"
        )
        val remoteMessage = RemoteMessage(bundle)
        firebaseNotifService.onCreate()
        firebaseNotifService.onMessageReceived(remoteMessage)
    }
}