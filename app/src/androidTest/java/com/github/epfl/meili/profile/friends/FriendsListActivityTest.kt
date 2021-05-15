package com.github.epfl.meili.profile.friends

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.models.User
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

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class FriendsListActivityTest {
    companion object {
        private const val TEST_CURRENT_USER_UID = "UID"
        private const val TEST_FRIEND_UID = "FRIEND_UID"
        private val TEST_FRIEND = Friend(TEST_FRIEND_UID)
        private const val TEST_FRIEND_NAME = "Friend"
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)

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
        mockAuthenticationService.signInIntent(null)


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

        Espresso.onView(textViewContainsText(TEST_FRIEND_NAME)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun friendChatIsDisplayedCorrectly() {
        val testFriendMap = HashMap<String, User>()
        testFriendMap[TEST_FRIEND_UID] = User(TEST_FRIEND_UID, TEST_FRIEND_NAME)

        val mockUserInfoService = Mockito.mock(UserInfoService::class.java)
        Mockito.`when`(mockUserInfoService.getUserInformation(Mockito.anyList(), Mockito.any(), Mockito.any())).then {
            val onSuccess = it.arguments[1] as ((Map<String, User>) -> Unit)

            onSuccess(testFriendMap)

            return@then null
        }

        FriendsListActivity.serviceProvider = { mockUserInfoService }

        database.onEvent(mockSnapshotAfterAddition, null)

        Espresso.onView(textViewContainsText(TEST_FRIEND_NAME)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.friend_chat_button)).perform(click())

        Intents.intended(IntentMatchers.toPackage("com.github.epfl.meili"))

        Espresso.onView(textViewContainsText(TEST_FRIEND_NAME)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(buttonViewContainsText("Send")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onAddFriendButtonLaunchIntent() {
        Espresso.onView(withId(R.id.add_friend_button)).perform(click())

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

    private fun buttonViewContainsText(content: String): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("A ButtonView with the text: $content")
            }

            override fun matchesSafely(item: View?): Boolean {
                when (item) {
                    is Button -> return item.text == content
                }
                return false
            }
        }
    }
}
