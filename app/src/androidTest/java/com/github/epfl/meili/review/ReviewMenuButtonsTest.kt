package com.github.epfl.meili.review

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.poi.PointOfInterest
import com.google.firebase.firestore.*
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ReviewMenuButtonsTest {

    companion object {
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
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterEdition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()
    private lateinit var database: FirestoreDatabase<Review>

    private var testAverageRatingBeforeAddition: Float = 0f
    private var testAverageRatingAfterAddition: Float = 0f
    private var testAverageRatingAfterEdition: Float = 0f

    init {
        setupMocks()
    }

    private fun setupMocks() {
        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<Review>
            Mockito.mock(ListenerRegistration::class.java)
        }
        `when`(mockCollection.document(ArgumentMatchers.matches(TEST_UID))).thenReturn(mockDocument)






        mockAuthenticationService.setMockUid(TEST_UID)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }

    private val intent =
        Intent(getInstrumentation().targetContext.applicationContext, ReviewsActivity::class.java)
            .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ReviewsActivity> = ActivityScenarioRule(intent)


    @Test
    fun clickChatMenuButton() {
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

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
        mockAuthenticationService.signInIntent()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(
            Matchers.allOf(
                withId(R.id.menu_forum_from_review), withText("Forum"),

                )
        ).perform(click())
    }
}