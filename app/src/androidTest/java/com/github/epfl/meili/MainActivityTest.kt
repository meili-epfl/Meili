package com.github.epfl.meili

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.FirebasePostService
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.Post.Companion.toPost
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.review.FirestoreReviewService
import com.google.firebase.firestore.*
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockRegistration: ListenerRegistration = Mockito.mock(ListenerRegistration::class.java)

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { _ ->
            mockRegistration
        }

        FirestoreReviewService.databaseProvider = { mockFirestore }
        FirestoreDatabase.databaseProvider = {mockFirestore}

        UiThreadStatement.runOnUiThread {
            val mockAuth = mock(AuthenticationService::class.java)

            `when`(mockAuth.getCurrentUser()).thenReturn(User("hi", "hi", "hi"))

            Auth.setAuthenticationService(mockAuth)
        }
    }

    @Before
    fun initializeMockDatabase() {
        val mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
        val mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val mockCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
        val postList = emptyList<Post>()

        Mockito.`when`(mockFirestore.collection("posts")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(ArgumentMatchers.any()))
            .thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.get()).thenAnswer { mockDocumentSnapshot }
        Mockito.`when`(mockCollectionReference.get()).thenAnswer { mockQuerySnapshot }
        Mockito.`when`(mockQuerySnapshot.documents.mapNotNull { it.toPost() }).thenReturn(postList)

        FirebasePostService.dbProvider = { mockFirestore }
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
        PermissionGranter.allowPermissionsIfNeeded("android.permissions.ACCESS_FINE_LOCATION")

        onView(withId(R.id.launchMapView)).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
    }

    @Test
    fun clickingOnReviewViewButtonShouldLaunchIntent() {
        onView(withId(R.id.launchReviewView)).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
    }

    @Test
    fun clickingOnForumViewShouldLaunchIntent() {
        onView(withId(R.id.launchForumView)).perform(click())

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