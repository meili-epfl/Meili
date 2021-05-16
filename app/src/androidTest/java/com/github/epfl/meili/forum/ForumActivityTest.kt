package com.github.epfl.meili.forum

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ForumActivityTest {

    companion object {
        private const val TEST_UID = "UID"
        private const val TEST_USERNAME = "AUTHOR"
        private const val TEST_POI_KEY = "lorem_ipsum2"
        private val TEST_POST = Post(TEST_POI_KEY, TEST_USERNAME, "TITLE", -1, "TEXT")
        private val TEST_POI = PointOfInterest(100.0, 100.0, "lorem_ipsum1", TEST_POI_KEY)
    }

    private val mockFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = mock(CollectionReference::class.java)
    private val mockComments: CollectionReference = mock(CollectionReference::class.java)
    private val mockPoiHistory: CollectionReference = mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    // transaction mocks
    private val transactionFunctionCaptor =
        ArgumentCaptor.forClass(Transaction.Function::class.java)
    private val mockTransaction = mock(Transaction::class.java)

    private lateinit var database: AtomicPostFirestoreDatabase
    private lateinit var commentsDatabase: FirestoreDatabase<Comment>
    private lateinit var poiDatabase: FirestoreDatabase<PointOfInterest>

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        ForumActivity::class.java
    ).putExtra(MapActivity.POI_KEY, TEST_POI)

    @get:Rule
    var rule: ActivityScenarioRule<ForumActivity> = ActivityScenarioRule(intent)

    @Before
    fun initIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    init {
        setupMocks()
        setupTransactionMocks()
        setupPostActivityMocks()
    }

    private fun setupTransactionMocks() {
        `when`(mockFirestore.runTransaction(transactionFunctionCaptor.capture()))
            .thenReturn(mock(Task::class.java))

        val mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockTransaction.get(any())).thenReturn(mockSnapshot)

        `when`(mockSnapshot.get("upvoters")).thenReturn(listOf(TEST_UID))
        `when`(mockSnapshot.get("downvoters")).thenReturn(listOf("OTHER_UID"))
    }

    private fun setupMocks() {
        `when`(mockFirestore.collection("forum")).thenReturn(
            mockCollection
        )

        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as AtomicPostFirestoreDatabase
            mock(ListenerRegistration::class.java)
        }
        `when`(mockCollection.document(contains(TEST_UID))).thenReturn(mockDocument)

        `when`(mockFirestore.collection("forum${TEST_UID}/comments")).thenReturn(mockComments)
        `when`(mockComments.addSnapshotListener(any())).thenAnswer { invocation ->
            commentsDatabase = invocation.arguments[0] as FirestoreDatabase<Comment>
            mock(ListenerRegistration::class.java)
        }
        `when`(mockComments.document(contains(TEST_UID))).thenReturn(mockDocument)

        `when`(mockSnapshotBeforeAddition.documents).thenReturn(ArrayList<DocumentSnapshot>())

        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.id).thenReturn(TEST_UID)
        `when`(mockDocumentSnapshot.toObject(Post::class.java)).thenReturn(TEST_POST)
        `when`(mockSnapshotAfterAddition.documents).thenReturn(listOf(mockDocumentSnapshot))

        // Mock poi history
        `when`(mockFirestore.collection("poi-history/${TEST_UID}/poi-history")).thenReturn(mockPoiHistory)
        `when`(mockPoiHistory.addSnapshotListener(any())).thenAnswer { invocation ->
            poiDatabase = invocation.arguments[0] as FirestoreDatabase<PointOfInterest>
            mock(ListenerRegistration::class.java)
        }
        `when`(mockPoiHistory.document(ArgumentMatchers.matches(TEST_POI_KEY))).thenReturn(mockDocument)

        mockAuthenticationService.setMockUid(TEST_UID)
        mockAuthenticationService.setUsername(TEST_USERNAME)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        FirebaseStorageService.storageProvider = { mock(FirebaseStorage::class.java) }
        Auth.authService = mockAuthenticationService
    }

    private fun setupPostActivityMocks() {
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
    }

    private fun textViewContainsText(content: String): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("A TextView with the text: $content")
            }

            override fun matchesSafely(item: View?): Boolean {
                when (item) {
                    is TextView -> return item.text == content
                }
                return false
            }
        }
    }

    @Test
    fun signedOutDisplayTest() {
        mockAuthenticationService.signOut()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(not(isDisplayed())))

        onView(withId(R.id.create_post)).check(matches(isNotEnabled()))
        onView(withId(R.id.create_post)).check(matches(not(isDisplayed())))
    }

    @Test
    fun signedInDisplayTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(not(isDisplayed())))

        onView(withId(R.id.create_post)).check(matches(isEnabled()))
        onView(withId(R.id.create_post)).check(matches(isDisplayed()))
    }

    @Test
    fun signedInCancelAddingTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.create_post)).perform(click())

        onView(withId(R.id.list_posts)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_post)).check(matches(isDisplayed()))

        onView(withId(R.id.cancel_post)).perform(click())

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(not(isDisplayed())))

        onView(withText(TEST_POST.title)).check(doesNotExist())
    }

    @Test
    fun signedInAddPostTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.create_post)).perform(click())

        onView(withId(R.id.post_edit_title)).perform(
            clearText(),
            typeText(TEST_POST.title),
            closeSoftKeyboard()
        )
        onView(withId(R.id.post_edit_text)).perform(
            clearText(),
            typeText(TEST_POST.text),
            closeSoftKeyboard()
        )

        onView(withId(R.id.submit_post)).perform(click())

        // send posts map with added post to the database
        database.onEvent(mockSnapshotAfterAddition, null)

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(not(isDisplayed())))

        onView(withId(R.id.forum_recycler_view))
            .check(matches(isDisplayed()))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(
                        withText(
                            TEST_POST.title
                        )
                    )
                )
            )

        onView(textViewContainsText(TEST_POST.title)).check(matches(isDisplayed()))
        onView(textViewContainsText(TEST_USERNAME)).check(matches(isDisplayed()))
    }

    @Test
    fun viewPostIntentsTest() {
        mockAuthenticationService.signOut()
        database.onEvent(mockSnapshotAfterAddition, null)
        onView(withId(R.id.forum_recycler_view))
            .check(matches(isDisplayed()))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(
                        withText(
                            TEST_POST.title
                        )
                    )
                )
            )

        onView(withText(TEST_POST.title)).perform(click())
        Intents.intended(
            allOf(
                hasExtra("Post", TEST_POST),
                hasComponent(PostActivity::class.java.name)
            )
        )
    }

    @Test

    fun clickOnSortingButtonTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)
        onView(withId(R.id.spinner)).perform(click())
    }

    @Test
    fun clickUpvoteDownvoteButtonsTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotAfterAddition, null)

        Thread.sleep(2000)

        onView(withId(R.id.upvote_button)).perform(click())
        transactionFunctionCaptor.value.apply(mockTransaction)
        onView(withId(R.id.downovte_button)).perform(click())
        transactionFunctionCaptor.value.apply(mockTransaction)
    }

        @Test
        fun useCameraIntentsTest() {
            mockAuthenticationService.signInIntent()
            database.onEvent(mockSnapshotBeforeAddition, null)

            onView(withId(R.id.create_post)).perform(click())

            onView(withId(R.id.post_use_camera)).perform(click())
            Intents.intended(hasComponent(CameraActivity::class.java.name))
        }



}