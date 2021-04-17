package com.github.epfl.meili.forum

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.MockAuthenticationService
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.Post.Companion.toPost
import com.github.epfl.meili.poi.PointOfInterest
import com.github.epfl.meili.review.ReviewsActivityViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ForumMenuButtonsTests {

    private val TEST_TEXT = "test text"
    private val TEST_TITLE = "test title"
    private val TEST_USERNAME = "test_username"
    private val TEST_EMAIL = "test@meili.com"
    private val postList = ArrayList<Post>()
    private val mockPost = Post("test id", TEST_USERNAME, TEST_TITLE, TEST_TEXT)
    private val mockList = ArrayList<DocumentSnapshot>()

    private val MOCK_PATH = "POI/mock-poi"
    private val fake_message = "fake_text"
    private val fake_id = "fake_id"
    private val fake_name = "fake_name_sender"
    private val fake_poi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "fake_poi", "fake_poi")

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockQuerySnapshot: QuerySnapshot

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

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
        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
        Mockito.`when`(mockFirestore.collection("posts")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.get()).thenAnswer { mockDocumentSnapshot }
        Mockito.`when`(mockCollectionReference.get()).thenAnswer { mockQuerySnapshot }
        Mockito.`when`(mockCollectionReference.add(any())).thenAnswer {
            mockList.add(mockDocumentSnapshot)
            mockTask  // Needs a Task, so I put a mock Task
        }
        Mockito.`when`(mockQuerySnapshot.documents.mapNotNull { it.toPost() }).thenReturn(postList)

        FirebasePostService.dbProvider = { mockFirestore }


    }

    @Before
    fun initiateAuthAndService() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            val mockAuthService = MockAuthenticationService()
            Auth.setAuthenticationService(mockAuthService)

            mockAuthService.signInIntent()

            Auth.isLoggedIn.value = true
            Auth.email = TEST_EMAIL
            Auth.name = TEST_USERNAME
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
    fun addPostToForumTest() {
        // Press Forum button
        Espresso.onView(ViewMatchers.withId(R.id.launchForumView))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        // Press + button
        Espresso.onView(ViewMatchers.withId(R.id.forum_new_post_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        // Type test title
        Espresso.onView(ViewMatchers.withId(R.id.new_post_title)).perform(
            ViewActions.typeText(TEST_TITLE),
            ViewActions.closeSoftKeyboard()
        )

        // Type test text
        Espresso.onView(ViewMatchers.withId(R.id.new_post_text)).perform(
            ViewActions.typeText(TEST_TEXT),
            ViewActions.closeSoftKeyboard()
        )

        // Press Create Post button
        Espresso.onView(ViewMatchers.withId(R.id.new_post_create_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())
    }

    @Test
    fun viewPostTest() {
        val mockPostService = MockPostService()
        ForumViewModel.changePostService(mockPostService)
        PostViewModel.changePostService(mockPostService)
        mockPostService.addPost(TEST_USERNAME, TEST_TITLE, TEST_TEXT)

        // Press Forum button
        Espresso.onView(ViewMatchers.withId(R.id.launchForumView))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        //Click on the mock post
        Espresso.onView(
            ViewMatchers.withChild(withText(TEST_TITLE))
        ).check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        // Check username
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.post_author),
                withText(TEST_USERNAME)
            )
        ).check(ViewAssertions.matches(withText(TEST_USERNAME)))
            .check(ViewAssertions.matches(isDisplayed()))

        // Check title
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.post_title),
                withText(TEST_TITLE)
            )
        ).check(ViewAssertions.matches(withText(TEST_TITLE)))
            .check(ViewAssertions.matches(isDisplayed()))

        // Check text
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.post_text),
                withText(TEST_TEXT)
            )
        ).check(ViewAssertions.matches(withText(TEST_TEXT)))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun clickChatMenuButton() {
        val mockPostService = MockPostService()
        ForumViewModel.changePostService(mockPostService)
        PostViewModel.changePostService(mockPostService)

        //Mock Chatting service to test chat button
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_PATH))
        ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10, fake_name)

        // Press Forum button
        Espresso.onView(ViewMatchers.withId(R.id.launchForumView))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        //click on the chat button
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.menu_chat_from_forum), withText("Chat")
            )
        ).perform(ViewActions.click())
    }

    @Test
    fun clickReviewMenuButton() {
        val mockPostService = MockPostService()
        ForumViewModel.changePostService(mockPostService)
        PostViewModel.changePostService(mockPostService)



        // Press Forum button
        Espresso.onView(ViewMatchers.withId(R.id.launchForumView))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        //click on the reviews  button
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.menu_reviews_from_forum), withText("Reviews")
            )
        ).perform(ViewActions.click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}