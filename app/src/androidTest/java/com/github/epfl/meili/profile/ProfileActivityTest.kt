package com.github.epfl.meili.profile

import android.location.LocationManager
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.User
import com.github.epfl.meili.posts.feed.FeedActivity
import com.github.epfl.meili.profile.friends.FriendsListActivity
import com.github.epfl.meili.util.LocationService
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    companion object {
        private const val MOCK_UID = "UID"
        private const val MOCK_USERNAME = "Meili User"
        private const val MOCK_BIO = "Hey There!"
        private val MOCK_USER = User(MOCK_UID, MOCK_USERNAME, "", MOCK_BIO)

        private const val TEST_USERNAME = "Basic User"
        private const val TEST_BIO = "I love travelling!"
        private val TEST_USER = User(MOCK_UID, TEST_USERNAME, "", TEST_BIO)
    }

    @get:Rule
    var testRule = ActivityScenarioRule(ProfileActivity::class.java)

    private val listenerCaptor: ArgumentCaptor<OnSuccessListener<DocumentSnapshot>> =
        ArgumentCaptor.forClass(OnSuccessListener::class.java) as ArgumentCaptor<OnSuccessListener<DocumentSnapshot>>
    private lateinit var mockDocumentSnapshot1: DocumentSnapshot
    private lateinit var mockDocumentSnapshot2: DocumentSnapshot

    init {
        setupMocks()
        setupMapMocks()
        setupStorageMocks()
        LocationService.getLocationManager = { mock(LocationManager::class.java) }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    private fun setupMocks() {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        `when`(mockFirestore.document("users/$MOCK_UID")).thenReturn(mockDocument)

        val mockTask = mock(Task::class.java)
        `when`(mockDocument.get()).thenReturn(mockTask as Task<DocumentSnapshot>?)

        `when`(mockTask.addOnSuccessListener(listenerCaptor.capture())).thenReturn(mockTask)

        mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot1.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot1.toObject(User::class.java)).thenReturn(MOCK_USER)

        mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot2.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot2.toObject(User::class.java)).thenReturn(TEST_USER)

        val mockAuthenticationService = MockAuthenticationService()
        mockAuthenticationService.setMockUid(MOCK_UID)
        mockAuthenticationService.setUsername(MOCK_USERNAME)

        FirestoreDocumentService.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
        mockAuthenticationService.signInIntent(null)
    }

    private fun setupMapMocks() {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
    }

    private fun setupStorageMocks() {
        val mockFirebase = mock(FirebaseStorage::class.java)
        val mockReference = mock(StorageReference::class.java)
        `when`(mockFirebase.getReference(ArgumentMatchers.anyString())).thenReturn(mockReference)

        val mockUploadTask = mock(UploadTask::class.java)
        `when`(mockReference.putBytes(ArgumentMatchers.any())).thenReturn(mockUploadTask)

        val mockStorageTask = mock(StorageTask::class.java)
        `when`(mockUploadTask.addOnSuccessListener(ArgumentMatchers.any())).thenReturn(
            mockStorageTask as StorageTask<UploadTask.TaskSnapshot>?
        )

        val mockTask = mock(Task::class.java)
        `when`(mockReference.downloadUrl).thenReturn(mockTask as Task<Uri>?)
        `when`(mockTask.addOnSuccessListener(ArgumentMatchers.any())).thenReturn(mockTask)

        FirebaseStorageService.storageProvider = { mockFirebase }
    }

    @Test
    fun canEditOwnedProfile() {
        runOnUiThread {
            listenerCaptor.value!!.onSuccess(mockDocumentSnapshot1)
        }

        onView(withId(R.id.profile_name)).check(matches(withText(MOCK_USERNAME)))
        onView(withId(R.id.profile_bio)).check(matches(withText(MOCK_BIO)))
        onView(withId(R.id.photo)).check(matches(isDisplayed()))

        onView(withId(R.id.photo_edit)).check(matches(not(isDisplayed())))
        onView(withId(R.id.save)).check(matches(not(isDisplayed())))
        onView(withId(R.id.cancel)).check(matches(not(isDisplayed())))

        onView(withId(R.id.profile_edit_button)).check(matches(isDisplayed()))
        onView(withId(R.id.list_friends_button)).check(matches(isDisplayed()))

        onView(withId(R.id.profile_posts_button)).check(matches(isDisplayed()))
        onView(withId(R.id.profile_poi_history_button)).check(matches(isDisplayed()))

        onView(withId(R.id.sign_out)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_in)).check(matches(not(isDisplayed())))
    }

    @Test
    fun profileEditSaveTest() {
        runOnUiThread {
            listenerCaptor.value!!.onSuccess(mockDocumentSnapshot1)
        }
        onView(withId(R.id.profile_name)).check(matches(withText(MOCK_USERNAME)))
        onView(withId(R.id.profile_bio)).check(matches(withText(MOCK_BIO)))
        onView(withId(R.id.photo)).check(matches(isDisplayed()))

        onView(withId(R.id.profile_edit_button)).perform(click())
        onView(withId(R.id.photo_edit)).check(matches(isDisplayed()))

        onView(withId(R.id.profile_edit_name)).perform(
            clearText(),
            typeText(TEST_USERNAME),
            closeSoftKeyboard()
        )
        onView(withId(R.id.profile_edit_bio)).perform(
            clearText(),
            typeText(TEST_BIO),
            closeSoftKeyboard()
        )
        onView(withId(R.id.save)).perform(click())

        runOnUiThread {
            listenerCaptor.value!!.onSuccess(mockDocumentSnapshot2)
        }

        onView(withId(R.id.profile_name)).check(matches(withText(TEST_USERNAME)))
        onView(withId(R.id.profile_bio)).check(matches(withText(TEST_BIO)))
    }

    @Test
    fun profileEditCancelTest() {
        runOnUiThread {
            listenerCaptor.value!!.onSuccess(mockDocumentSnapshot1)
        }
        onView(withId(R.id.profile_name)).check(matches(withText(MOCK_USERNAME)))
        onView(withId(R.id.profile_bio)).check(matches(withText(MOCK_BIO)))
        onView(withId(R.id.photo)).check(matches(isDisplayed()))

        onView(withId(R.id.profile_edit_button)).perform(click())

        onView(withId(R.id.profile_edit_name)).perform(
            clearText(),
            typeText(TEST_USERNAME),
            closeSoftKeyboard()
        )
        onView(withId(R.id.profile_edit_bio)).perform(
            clearText(),
            typeText(TEST_BIO),
            closeSoftKeyboard()
        )
        onView(withId(R.id.cancel)).perform(click())

        onView(withId(R.id.profile_name)).check(matches(withText(MOCK_USERNAME)))
        onView(withId(R.id.profile_bio)).check(matches(withText(MOCK_BIO)))
    }

    @Test
    fun clickingOnFriendsListShouldLaunchIntent() {
        onView(withId(R.id.list_friends_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FriendsListActivity::class.qualifiedName))
    }

    @Test
    fun signOutTest() {
        onView(withId(R.id.sign_out)).perform(click())
        onView(withId(R.id.signed_in)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()))
    }

    @Test
    fun goToMapTest() {
        onView(withId(R.id.map_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MapActivity::class.qualifiedName))
    }

    @Test
    fun goToFeedTest() {
        onView(withId(R.id.feed_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FeedActivity::class.qualifiedName))
    }

    @Test
    fun changeModeTest() {
        onView(withId(R.id.switch_mode)).perform(click())
    }

}