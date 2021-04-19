package com.github.epfl.meili.forum


import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.poi.PointOfInterest
import com.github.epfl.meili.review.ReviewsActivity
import com.github.epfl.meili.review.ReviewsActivityTest
import com.google.firebase.firestore.*
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ForumMenuButtonsTests {

    companion object {
        private const val TEST_UID = "UID"
        private const val TEST_USERNAME = "AUTHOR"
        private val TEST_POST = Post(TEST_USERNAME, "TITLE", "TEXT")
        private const val MOCK_PATH = "POI/mock-poi"
        private const val fake_message = "fake_text"
        private const val fake_id = "fake_id"
        private const val fake_name = "fake_name_sender"
        private val TEST_POI_KEY = PointOfInterest(100.0,100.0,"lorem_ipsum1", "lorem_ipsum2")
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    private lateinit var database: FirestoreDatabase<Post>

    private val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext, ForumActivity::class.java)
        .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ForumActivity> = ActivityScenarioRule(intent)

    init {
        setupMocks()
    }

    private fun setupMocks() {
        Mockito.`when`(mockFirestore.collection((any()))).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<Post>
            Mockito.mock(ListenerRegistration::class.java)
        }
        Mockito.`when`(mockCollection.document(ArgumentMatchers.contains(TEST_UID)))
            .thenReturn(mockDocument)

        Mockito.`when`(mockSnapshotBeforeAddition.documents).thenReturn(ArrayList<DocumentSnapshot>())

        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn(TEST_UID)
        Mockito.`when`(mockDocumentSnapshot.toObject(Post::class.java))
            .thenReturn(TEST_POST)
        Mockito.`when`(mockSnapshotAfterAddition.documents).thenReturn(listOf(mockDocumentSnapshot))

        mockAuthenticationService.setMockUid(TEST_UID)
        mockAuthenticationService.setUsername(TEST_USERNAME)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
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
    fun clickChatMenuButton() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        //Mock Chatting service to test chat button
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_PATH))
        ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10, fake_name)


        //click on the chat button
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.menu_chat_from_forum), withText("Chat")
            )
        ).perform(ViewActions.click())
    }

    @Test
    fun clickReviewMenuButton() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)


        //click on the reviews  button
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.menu_reviews_from_forum), withText("Reviews")
            )
        ).perform(ViewActions.click())
    }


}