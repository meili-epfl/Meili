package com.github.epfl.meili.review

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.FirebasePostService
import com.github.epfl.meili.forum.ForumViewModel
import com.github.epfl.meili.forum.MockPostService
import com.github.epfl.meili.forum.PostViewModel
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.Post.Companion.toPost
import com.github.epfl.meili.models.Review
import com.google.android.gms.maps.model.LatLng
import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ReviewMenuButtonsTest {

    companion object {
        private const val TAG = "ReviewsActivityTest"

        private const val TEST_UID = "MrPerfect"

        //Review now Takes entire POI with the intent instead of a string
        private val TEST_POI_KEY = PointOfInterest(100.0, 100.0, "lorem_ipsum1", "lorem_ipsum2")
        private const val TEST_TITLE = "Beach too sandy"
        private const val TEST_SUMMARY = "Water too wet"

        private const val AVERAGE_FORMAT = "%.2f"

        private const val NUM_REVIEWS_BEFORE_ADDITION = 10

        private const val ADDED_REVIEW_RATING = 0.5f
        private const val TEST_ADDED_TITLE = "Desert too sandy"

        private const val EDITED_REVIEW_RATING = 5f
        private const val TEST_EDITED_TITLE = "Looks good to me"

        private const val MOCK_PATH = "POI/mock-poi"
        private const val fake_message = "fake_text"
        private const val fake_id = "fake_id"
        private const val fake_name = "fake_name_sender"

        private const val TEST_USERNAME = "test_username"
        private const val TEST_EMAIL = "test@meili.com"

    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)
    private val mockListenerRegistration: ListenerRegistration =
        Mockito.mock(ListenerRegistration::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterEdition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockDocumentReference: DocumentReference = Mockito.mock(DocumentReference::class.java)
    private val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    private val mockCollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockList = ArrayList<DocumentSnapshot>()
    private val postList = ArrayList<Post>()

    private val mockAuthenticationService = MockAuthenticationService()
    private lateinit var database: FirestoreDatabase<Review>


    init {
        setupMocks()
    }

    private fun setupMocks() {
        mockAuthenticationService.setMockUid(TEST_UID)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            (invocation.arguments[0] as FirestoreDatabase<Review>).also { database = it }
            mockListenerRegistration
        }
        `when`(mockCollection.document(Mockito.matches(TEST_UID))).thenReturn(mockDocument)

        val mockTask = Mockito.mock(Task::class.java)


        val mockCollection = Mockito.mock(CollectionReference::class.java)
        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
        `when`(mockFirestore.collection("posts")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.get()).thenAnswer { mockDocumentSnapshot }
        `when`(mockCollectionReference.get()).thenAnswer { mockQuerySnapshot }
        `when`(mockCollectionReference.add(any())).thenAnswer {
            mockList.add(mockDocumentSnapshot)
            mockTask  // Needs a Task, so I put a mock Task
        }
        `when`(mockQuerySnapshot.documents.mapNotNull { it.toPost() }).thenReturn(postList)

        FirebasePostService.dbProvider = { mockFirestore }
        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }


    private val intent =
        Intent(getInstrumentation().targetContext.applicationContext, ReviewsActivity::class.java)
            .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ReviewsActivity> = ActivityScenarioRule(intent)

    @Before
    fun initiateAuthAndService() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            val mockAuthService = com.github.epfl.meili.home.MockAuthenticationService()
            Auth.setAuthenticationService(mockAuthService)

            mockAuthService.signInIntent()

            Auth.isLoggedIn.value = true
            Auth.email = TEST_EMAIL
            Auth.name = TEST_USERNAME
        }
    }

    @Test
    fun clickChatMenuButton() {
        //Mock Chatting service to test chat button
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_PATH))
        ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10, fake_name)

        onView(
            Matchers.allOf(
                withId(R.id.menu_chat_from_review), withText("Chat"),
            )
        ).perform(click())
    }

    @Test
    fun clickForumMenuButton() {
        val mockPostService = MockPostService()
        ForumViewModel.changePostService(mockPostService)
        PostViewModel.changePostService(mockPostService)

        onView(
            Matchers.allOf(
                withId(R.id.menu_forum_from_review), withText("Forum"),

                )
        ).perform(click())
    }
}