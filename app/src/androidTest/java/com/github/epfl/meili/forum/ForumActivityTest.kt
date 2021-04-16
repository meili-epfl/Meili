package com.github.epfl.meili.forum

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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Post
import com.google.firebase.firestore.*
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ForumActivityTest {

    companion object {
        private const val TEST_UID = "UID"
        private val TEST_POST = Post("AUTHOR", "TITLE", "TEXT")
    }

    private val mockFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = mock(DocumentReference::class.java)
    private val mockListenerRegistration: ListenerRegistration = mock(ListenerRegistration::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    private lateinit var database: FirestoreDatabase<Post>

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initializeMockDatabase() {
        `when`(mockFirestore.collection("posts")).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            (invocation.arguments[0] as FirestoreDatabase<Post>).also { database = it }
            mockListenerRegistration
        }
        `when`(mockCollection.document(contains(TEST_UID))).thenReturn(mockDocument)

        `when`(mockSnapshotBeforeAddition.documents).thenReturn(ArrayList<DocumentSnapshot>())

        val mockDocumentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.id).thenReturn(TEST_UID)
        `when`(mockDocumentSnapshot.toObject(Post::class.java)).thenReturn(TEST_POST)
        `when`(mockSnapshotAfterAddition.documents).thenReturn(listOf(mockDocumentSnapshot))

        mockAuthenticationService.setMockUid(TEST_UID)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }

    @Test
    fun signedOutDisplayTest() {
        mockAuthenticationService.signOut()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(CoreMatchers.not(isDisplayed())))

        onView(withId(R.id.create_post)).check(matches(isNotEnabled()))
        onView(withId(R.id.create_post)).check(matches(CoreMatchers.not(isDisplayed())))
    }

    @Test
    fun signedInDisplayTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(CoreMatchers.not(isDisplayed())))

        onView(withId(R.id.create_post)).check(matches(isEnabled()))
        onView(withId(R.id.create_post)).check(matches(isDisplayed()))
    }

    @Test
    fun signedInCancelAddingTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.create_post)).perform(click())

        onView(withId(R.id.list_posts)).check(matches(CoreMatchers.not(isDisplayed())))
        onView(withId(R.id.edit_post)).check(matches(isDisplayed()))

        onView(withId(R.id.cancel_post)).perform(click())

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(CoreMatchers.not(isDisplayed())))

        onView(withText(TEST_POST.title)).check(doesNotExist())
    }

    @Test
    fun signedInAddPostTest() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.create_post)).perform(click())

        onView(withId(R.id.post_edit_title)).perform(clearText(), typeText(TEST_POST.title), closeSoftKeyboard())
        onView(withId(R.id.post_edit_text)).perform(clearText(), typeText(TEST_POST.text), closeSoftKeyboard())

        onView(withId(R.id.submit_post)).perform(click())

        // send posts map with added review to review service
        database.onEvent(mockSnapshotAfterAddition, null)

        onView(withId(R.id.list_posts)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_post)).check(matches(CoreMatchers.not(isDisplayed())))

        onView(withId(R.id.forum_recycler_view))
                .check(matches(isDisplayed()))
                .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(TEST_POST.title))))

        onView(withText(TEST_POST.title)).check(matches(isDisplayed()))
        onView(textViewContainsText(TEST_UID)).check(matches(isDisplayed()))
    }

    @Test
    fun viewPostTest() {
        onView(withId(R.id.forum_recycler_view))
                .check(matches(isDisplayed()))
                .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(TEST_POST.title))))

        Intents.init()
        onView(withText(TEST_POST.title)).perform(click())
        Intents.intended(hasComponent(PostActivity::class.java.name))
        Intents.release()
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
}