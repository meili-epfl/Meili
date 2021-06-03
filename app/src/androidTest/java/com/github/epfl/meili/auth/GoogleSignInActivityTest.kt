package com.github.epfl.meili.auth

import android.app.Instrumentation
import android.content.Intent
import android.location.LocationManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.util.LocationService
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
class GoogleSignInActivityTest {
    private lateinit var mockService: MockAuthenticationService

    companion object {
        private const val MOCK_NAME = "Fake Name"
    }

    init {
        setupMapMocks()
    }

    private fun setupMapMocks() {
        val mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
        val mockCollection = Mockito.mock(CollectionReference::class.java)
        Mockito.`when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(ArgumentMatchers.any()))
            .thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
        LocationService.getLocationManager = { Mockito.mock(LocationManager::class.java) }
        mockService = MockAuthenticationService()
        Auth.authService = mockService
    }

    @get:Rule
    var testRule: ActivityScenarioRule<GoogleSignInActivity> = ActivityScenarioRule(
        GoogleSignInActivity::class.java
    )

    @Before
    fun signOut() {
        runOnUiThread {
            Auth.signOut()
        }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @Before
    fun removePopUps() {
        testRule.scenario.onActivity {
            it.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun whenIsLoggedInGoesToMap() {
        runOnUiThread {
            Auth.name = MOCK_NAME
            Auth.isLoggedIn.value = true
        }

        Intents.intended(IntentMatchers.hasComponent(MapActivity::class.qualifiedName))
    }

    @Test
    fun loggedOutInterfaceTest() {
        onView(withId(R.id.textFieldSignIn)).check(matches(withText("")))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign In")))
    }

    @Test
    fun onActivityResultTest() {
        onView(withId(R.id.signInButton)).check(matches(isClickable())).perform(click())

        val resultData = Intent()
        resultData.putExtra("name", MOCK_NAME)
        val result = Instrumentation.ActivityResult(9001, resultData)

        val mockIntent = mockService.signInIntent(null)

        intending(IntentMatchers.filterEquals(mockIntent)).respondWith(result)
    }
}
