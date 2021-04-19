package com.github.epfl.meili.forum


import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.poi.PointOfInterest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ForumMenuButtonsTests {

    companion object {
        private const val MOCK_CHAT_PATH = "POI/mock-poi"
        private val TEST_POI = PointOfInterest(100.0,100.0,"lorem_ipsum1", "lorem_ipsum2")
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)

    private lateinit var database: FirestoreDatabase<Post>

    private val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext, ForumActivity::class.java)
        .putExtra("POI", TEST_POI)

    @get:Rule
    var rule: ActivityScenarioRule<ForumActivity> = ActivityScenarioRule(intent)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Before
    fun setupMocks() {
        Mockito.`when`(mockFirestore.collection((any()))).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<Post>
            Mockito.mock(ListenerRegistration::class.java)
        }

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = Mockito.mock(AuthenticationService::class.java)
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_CHAT_PATH))
    }

    @Test
    fun clickChatMenuButton() {
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.menu_chat_from_forum), withText("Chat")
            )
        ).perform(ViewActions.click())
    }

    @Test
    fun clickReviewMenuButton() {
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.menu_reviews_from_forum), withText("Reviews")
            )
        ).perform(ViewActions.click())
    }
}