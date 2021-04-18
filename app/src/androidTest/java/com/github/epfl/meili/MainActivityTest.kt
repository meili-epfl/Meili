package com.github.epfl.meili

import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.storage.FirebaseStorageService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()

        val mockFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection: CollectionReference = mock(CollectionReference::class.java)
        val mockRegistration: ListenerRegistration = mock(ListenerRegistration::class.java)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { mockRegistration }

        FirestoreDatabase.databaseProvider = { mockFirestore }

        val mockFirebase = mock(FirebaseStorage::class.java)
        val mockReference = mock(StorageReference::class.java)
        `when`(mockFirebase.getReference(ArgumentMatchers.anyString())).thenReturn(mockReference)

        val mockUploadTask = mock(UploadTask::class.java)
        `when`(mockReference.putBytes(any())).thenReturn(mockUploadTask)

        val mockStorageTask = mock(StorageTask::class.java)
        `when`(mockUploadTask.addOnSuccessListener(any())).thenReturn(mockStorageTask as StorageTask<UploadTask.TaskSnapshot>?)

        val mockTask = mock(Task::class.java)
        `when`(mockReference.downloadUrl).thenReturn(mockTask as Task<Uri>?)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)

        FirebaseStorageService.storageProvider = { mockFirebase }

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

    @Test
    fun clickingOnPhotoViewShouldLaunchIntent() {
        onView(withId(R.id.launchPhotoView)).perform(click())

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