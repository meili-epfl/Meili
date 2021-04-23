package com.github.epfl.meili.review

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.MockMessageDatabase
import com.github.epfl.meili.models.PointOfInterest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@RunWith(AndroidJUnit4::class)
class ReviewMenuButtonsTest {

    companion object {
        private val TEST_POI_KEY = PointOfInterest(100.0, 100.0, "lorem_ipsum1", "lorem_ipsum2")
        private const val MOCK_CHAT_PATH = "POI/mock-poi"
    }

    init {
        val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
        val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { Mockito.mock(ListenerRegistration::class.java) }

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = MockAuthenticationService()
        Auth.authService.signInIntent()
        ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_CHAT_PATH))
    }

    private val intent =
        Intent(getInstrumentation().targetContext.applicationContext, ReviewsActivity::class.java)
            .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ReviewsActivity> = ActivityScenarioRule(intent)

    @Test
    fun clickChatMenuButton() {
        onView(
            Matchers.allOf(
                    withId(R.id.menu_chat),
                    withText("Chat")
            )
        ).perform(click())
    }

    @Test
    fun clickForumMenuButton() {
        onView(
            Matchers.allOf(
                    withId(R.id.menu_forum),
                    withText("Forum")
                )
        ).perform(click())
    }
}