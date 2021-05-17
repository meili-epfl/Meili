package com.github.epfl.meili.messages


import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.google.firebase.firestore.*
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito


@LargeTest
class ChatLogMenuButtonsTest {

    companion object {
        private const val TEST_UID = "UID"
        private const val TEST_USERNAME = "AUTHOR"
        private val TEST_POST = Post(TEST_USERNAME, "TITLE", -1,"TEXT")
        private const val MOCK_PATH = "POI/mock-poi"
        private const val fake_message = "fake_text"
        private const val fake_id = "fake_id"
        private const val fake_name = "fake_name_sender"
        private val fake_poi = PointOfInterest(10.0, 10.0, "fakepoi1", "fakepoi2")
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockAuth = Mockito.mock(AuthenticationService::class.java)

    private lateinit var database: FirestoreDatabase<Post>

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        ChatLogActivity::class.java
    )
        .putExtra("POI_KEY", fake_poi)

    @get:Rule
    var rule: ActivityScenarioRule<ChatLogActivity> = ActivityScenarioRule(intent)

    init {
        setupMocks()
    }

    private fun setupMocks() {
        Mockito.`when`(mockFirestore.collection((ArgumentMatchers.any())))
            .thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(ArgumentMatchers.any()))
            .thenAnswer { invocation ->
                Mockito.mock(ListenerRegistration::class.java)
            }
        Mockito.`when`(mockCollection.document(ArgumentMatchers.contains(TEST_UID)))
            .thenReturn(mockDocument)

        Mockito.`when`(mockSnapshotBeforeAddition.documents)
            .thenReturn(ArrayList<DocumentSnapshot>())



        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn(TEST_UID)
        Mockito.`when`(mockDocumentSnapshot.toObject(Post::class.java))
            .thenReturn(TEST_POST)
        Mockito.`when`(mockSnapshotAfterAddition.documents).thenReturn(listOf(mockDocumentSnapshot))


        UiThreadStatement.runOnUiThread {


            Mockito.`when`(mockAuth.getCurrentUser())
                .thenReturn(User("fake_uid", "fake_name", "fake_email"))

            Mockito.`when`(mockAuth.signInIntent()).thenReturn(intent)
            Auth.setAuthenticationService(mockAuth)
        }

        // Inject dependencies
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_PATH))
        ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10, fake_name)
        FirestoreDatabase.databaseProvider = { mockFirestore }
        //AtomicPostFirestoreDatabase.databaseProvider = { mockFirestore }
    }

    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {

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
    fun clickForumMenuButton() {
        onView(
            allOf(
                withId(R.id.menu_forum), withText("Forum"),
            )
        ).perform(click())
    }


    @Test
    fun clickReviewMenuButton() {
        onView(
            allOf(
                withId(R.id.menu_reviews), withText("Reviews"),
            )
        ).perform(click())
    }


}
