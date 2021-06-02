package com.github.epfl.meili.notifications

import androidx.core.os.bundleOf
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FirebaseNotificationServiceTest {

    private val test_token: String = "4"
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
        //FirebaseNotificationService.token = test_token
        //assertThat(FirebaseNotificationService.token, `is`(test_token))
    }
    @Test
    fun messageReceivedCorrectly(){

    }
}