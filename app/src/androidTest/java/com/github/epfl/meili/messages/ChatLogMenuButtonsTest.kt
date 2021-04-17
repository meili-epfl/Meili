package com.github.epfl.meili.messages


import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.FirebasePostService
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.Post.Companion.toPost
import com.github.epfl.meili.models.User
import com.google.android.gms.maps.model.LatLng
import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito


@LargeTest
class ChatLogMenuButtonsTest {

    private val MOCK_PATH = "POI/mock-poi"
    private val fake_message = "fake_text"
    private val fake_id = "fake_id"
    private val fake_name = "fake_name_sender"
    private val fake_poi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "fake_poi", "fake_poi")
    private val MOCK_EMAIL = "moderator2@gmail.com"
    private val MOCK_PASSWORD = "123123"

    private val TEST_TEXT = "test text"
    private val TEST_TITLE = "test title"
    private val TEST_USERNAME = "test_username"
    private val TEST_EMAIL = "test@meili.com"
    private val postList = ArrayList<Post>()
    private val mockPost = Post("test id", TEST_USERNAME, TEST_TITLE, TEST_TEXT)
    private val mockList = ArrayList<DocumentSnapshot>()


    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockQuerySnapshot: QuerySnapshot

    @get:Rule
    val mActivityTestRule: ActivityTestRule<ChatLogActivity> =
        object : ActivityTestRule<ChatLogActivity>(ChatLogActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

                val intent = Intent(targetContext, ChatLogActivity::class.java).apply {
                    putExtra("POI_KEY", fake_poi)
                }

                UiThreadStatement.runOnUiThread {
                    val mockAuth = Mockito.mock(AuthenticationService::class.java)

                    Mockito.`when`(mockAuth.getCurrentUser())
                        .thenReturn(User("fake_uid", "fake_name", "fake_email"))

                    Mockito.`when`(mockAuth.signInIntent()).thenReturn(intent)
                    Auth.setAuthenticationService(mockAuth)
                }

                return intent

            }
        }


    @Before
    fun initializeMockDatabase() {
        postList.add(mockPost)

        mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
        mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        mockCollectionReference = Mockito.mock(CollectionReference::class.java)
        mockDocumentReference = Mockito.mock(DocumentReference::class.java)
        mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
        val mockTask = Mockito.mock(Task::class.java)


        val mockCollection = Mockito.mock(CollectionReference::class.java)
        Mockito.`when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(ArgumentMatchers.any())).thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
        Mockito.`when`(mockFirestore.collection("posts")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(ArgumentMatchers.any())).thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.get()).thenAnswer { mockDocumentSnapshot }
        Mockito.`when`(mockCollectionReference.get()).thenAnswer { mockQuerySnapshot }
        Mockito.`when`(mockCollectionReference.add(ArgumentMatchers.any())).thenAnswer {
            mockList.add(mockDocumentSnapshot)
            mockTask  // Needs a Task, so I put a mock Task
        }
        Mockito.`when`(mockQuerySnapshot.documents.mapNotNull { it.toPost() }).thenReturn(postList)

        FirebasePostService.dbProvider = { mockFirestore }


    }

    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_PATH))
            ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10, fake_name)
        }
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
    fun clickForumMenuButton(){
        onView(
            allOf(
                withId(R.id.menu_forum_from_chat), withText("Forum"),
            )
        ).perform(click())
    }

    @Test
    fun clickReviewMenuButton(){
        onView(
            allOf(
                withId(R.id.menu_reviews_from_chat), withText("Reviews"),
            )
        ).perform(click())
    }


}
