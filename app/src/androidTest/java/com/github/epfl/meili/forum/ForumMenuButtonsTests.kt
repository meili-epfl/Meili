package com.github.epfl.meili.forum


import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.review.MockAuthenticationService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ForumMenuButtonsTests {

    companion object {
        private const val MOCK_CHAT_PATH = "POI/mock-poi"
        private val TEST_POI_KEY = PointOfInterest(100.0,100.0,"lorem_ipsum1", "lorem_ipsum2")
    }

    init {
        val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
        val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = MockAuthenticationService()
        Auth.authService.signInIntent()
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_CHAT_PATH))
    }

    private val intent =
            Intent(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext, ForumActivity::class.java)
                    .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ForumActivity> = ActivityScenarioRule(intent)


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