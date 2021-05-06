package com.github.epfl.meili.profile.friends

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.firebase.firestore.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class FriendsListActivityTest {
    companion object {
        private const val TEST_CURRENT_USER_UID = "UID"
        private const val TEST_FRIEND_UID = "FRIEND_UID"
        private val TEST_FRIEND = Friend(TEST_FRIEND_UID)
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    private lateinit var database: FirestoreDatabase<Friend>

    @get:Rule
    var rule: ActivityScenarioRule<FriendsListActivity> = ActivityScenarioRule(FriendsListActivity::class.java)

    @Before
    fun initIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    init {
        setupMocks()
    }

    private fun setupMocks() {


        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn(TEST_FRIEND_UID)
        Mockito.`when`(mockDocumentSnapshot.toObject(Friend::class.java)).thenReturn(TEST_FRIEND)
        Mockito.`when`(mockSnapshotAfterAddition.documents).thenReturn(listOf(mockDocumentSnapshot))

        mockAuthenticationService.setMockUid(TEST_CURRENT_USER_UID)
        mockAuthenticationService.setUsername(TEST_CURRENT_USER_UID)
        mockAuthenticationService.signInIntent()


        Mockito.`when`(mockCollection.document(ArgumentMatchers.contains(TEST_FRIEND_UID))).thenReturn(mockDocument)

        Mockito.`when`(mockCollection.addSnapshotListener(ArgumentMatchers.any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<Friend>
            Mockito.mock(ListenerRegistration::class.java)
        }
        Mockito.`when`(mockFirestore.collection(Mockito.anyString())).thenReturn(mockCollection)
        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }

    @Test
    fun uiDisplaysCorrectInfo() {
        database.onEvent(mockSnapshotAfterAddition, null)

        Espresso.onView(textViewContainsText(TEST_FRIEND_UID)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onAddFriendButtonLaunchIntent() {
        Espresso.onView(ViewMatchers.withId(R.id.add_friend_button)).perform(ViewActions.click())

        Intents.intended(IntentMatchers.toPackage("com.github.epfl.meili"))
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