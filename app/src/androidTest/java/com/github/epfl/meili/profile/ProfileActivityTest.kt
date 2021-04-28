package com.github.epfl.meili.profile

import com.github.epfl.meili.profile.MockAuthenticationService
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test
import org.mockito.Mockito

class ProfileActivityTest {
    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockAuthenticationService = MockAuthenticationService()

    @Test
    fun displayProfileTest(){
        mockAuthenticationService.signInIntent()

    }
}