package com.github.epfl.meili.notifications

import androidx.core.os.bundleOf
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FirebaseNotificationServiceTest {
    @Before
    fun initMock(){
        val mockAuth = Mockito.mock(AuthenticationService::class.java)

        Mockito.`when`(mockAuth.getCurrentUser())
            .thenReturn(User("fake_uid", "fake_name", "fake_email", " "))

        Auth.setAuthenticationService(mockAuth)
    }

    @Test
    fun onNewTokenUpdatesToken(){
        FirebaseNotificationService.token = "4"
    }
    @Test
    fun messageReceivedCorrectly(){

    }
}