package com.github.epfl.meili.notifications


import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.auth.AuthenticationService

import com.github.epfl.meili.models.User

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