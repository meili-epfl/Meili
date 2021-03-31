package com.github.epfl.meili

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.review.FirestoreReviewService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.w3c.dom.Document

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
        UiThreadStatement.runOnUiThread {
            val mockAuth = mock(AuthenticationService::class.java)

            `when`(mockAuth.getCurrentUser()).thenReturn(User("hi", "hi", "hi"))

            Auth.setAuthenticationService(mockAuth)
        }
    }

    @After
    fun cleanup() {
        Intents.release()
    }

    @Test
    fun clickingOnSignInViewButtonShouldLaunchIntent() {
        onView(withId(R.id.launchSignInView)).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
    }

    @Test
    fun clickingOnChatViewButtonShouldLaunchIntent() {

        assertEquals(Auth.getCurrentUser(), User("hi", "hi", "hi"))
        onView(withId(R.id.launchChatView)).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
    }

    @Test
    fun clickingOnMapViewButtonShouldLaunchIntent() {
        onView(withId(R.id.launchMapView)).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
    }

    @Test
    fun clickingOnReviewViewButtonShouldLaunchIntent() {
        val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
        val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockRegistration: ListenerRegistration = Mockito.mock(ListenerRegistration::class.java)
        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { _ ->
            mockRegistration
        }
        FirestoreReviewService.databaseProvider = { mockFirestore }

        onView(withId(R.id.launchReviewView)).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
    }

//    @Test
//    fun testNavigation() {
//        PermissionGranter.allowPermissionsIfNeeded("android.permissions.ACCESS_FINE_LOCATION")
//        onView(withId(R.id.launchSignInView)).perform(click())
//        onView(withId(R.id.signInButton)).check(matches(isDisplayed()))
//        pressBack()
//        onView(withId(R.id.launchMap)).perform(click())
//        onView(withId(R.id.map)).check(matches(isDisplayed()))
//        pressBack()
//        onView(withId(R.id.launchSignInView)).check(matches(isClickable())).perform(click())
//    }
}