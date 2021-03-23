package com.github.epfl.meili.forum

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
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
import com.github.epfl.meili.forum.Post.Companion.toPost
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.MockAuthenticationService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
class ForumTest {

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

        Mockito.`when`(mockFirestore.collection("posts")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.get()).thenAnswer { mockDocumentSnapshot }
        Mockito.`when`(mockCollectionReference.get()).thenAnswer { mockQuerySnapshot }
        Mockito.`when`(mockCollectionReference.add(any())).thenAnswer{
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

        Thread.sleep(5000)  // wait for posts to load
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