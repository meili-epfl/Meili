package com.github.epfl.meili.profile

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.forum.MockAuthenticationService
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    companion object {
        private const val MOCK_UID = "UID"
        private const val MOCK_USERNAME = "Meili User"
        private const val MOCK_BIO = "Hey There!"
        private val MOCK_USER = User(MOCK_UID, MOCK_USERNAME, "", MOCK_BIO)

        private const val TEST_USERNAME = "Basic User"
        private const val TEST_BIO = "I love travelling!"
    }

    @get:Rule
    var testRule = ActivityScenarioRule(ProfileActivity::class.java)

    private val listenerCaptor = ArgumentCaptor.forClass(OnSuccessListener::class.java)
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    init {
        setupMocks()
    }

    private fun setupMocks() {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        `when`(mockFirestore.document("users/$MOCK_UID")).thenReturn(mockDocument)

        val mockTask = mock(Task::class.java)
        `when`(mockDocument.get()).thenReturn(mockTask as Task<DocumentSnapshot>?)

//        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)

        mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.toObject(User::class.java)).thenReturn(MOCK_USER)

        val mockAuthenticationService = MockAuthenticationService()
        mockAuthenticationService.setMockUid(MOCK_UID)
        mockAuthenticationService.setUsername(MOCK_USERNAME)

        FirestoreDocumentService.databaseProvider = { mockFirestore }
        FirebaseStorageService.storageProvider = { mock(FirebaseStorage::class.java)}
        Auth.authService = mockAuthenticationService
        mockAuthenticationService.signInIntent()
    }

    @Test
    fun profileEditSaveTest() {
        onView(withId(R.id.name)).check(matches(withText(MOCK_USERNAME)))
        onView(withId(R.id.bio)).check(matches(withText("")))
        onView(withId(R.id.photo)).check(matches(isDisplayed()))

        onView(withId(R.id.name)).perform(clearText(), typeText(TEST_USERNAME), closeSoftKeyboard())
        onView(withId(R.id.bio)).perform(clearText(), typeText(TEST_BIO), closeSoftKeyboard())
        onView(withId(R.id.save)).perform(click())

//        runOnUiThread {
//            (listenerCaptor.value!! as OnSuccessListener<DocumentSnapshot>).onSuccess(mockDocumentSnapshot)
//        }

        onView(withId(R.id.name)).check(matches(withText(TEST_USERNAME)))
        onView(withId(R.id.bio)).check(matches(withText(TEST_BIO)))
    }
}