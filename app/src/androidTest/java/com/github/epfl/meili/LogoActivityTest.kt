package com.github.epfl.meili

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class LogoActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(LogoActivity::class.java)

    init  {
        val mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
        val mockCollection = Mockito.mock(CollectionReference::class.java)
        Mockito.`when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(ArgumentMatchers.any()))
            .thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
        LogoActivity.authenticationService = { MockAuthenticationService() }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun launchingTriggersIntent() {
        Thread.sleep(1000) // logo activity purposefully delays a little
        Intents.intended(IntentMatchers.isInternal())
    }
}