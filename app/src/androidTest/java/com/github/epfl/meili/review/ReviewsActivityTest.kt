package com.github.epfl.meili.review

import android.content.Intent
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.ChatLogActivity.Companion.TAG
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review
import com.google.firebase.firestore.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import java.lang.Thread.sleep


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class CompleteReviewsActivityTest {

    companion object {
        private const val TEST_POI_KEY = "poiKey"
        private const val TEST_TITLE = "Beach too sandy"
        private const val TEST_SUMMARY = "Water too wet"
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private lateinit var service: FirestoreReviewService
    private val mockSnapshot: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockListenerRegistration: ListenerRegistration = Mockito.mock(ListenerRegistration::class.java)

    private lateinit var testReviews: Map<String, Review>
    private var testAverageRating: Float

    init {
        val rs: MutableMap<String, Review> = HashMap()
        for (i in 1..10) {
            rs[i.toString()] = Review(i.toFloat() / 2, TEST_TITLE, TEST_SUMMARY)
        }

        testReviews = rs
        testAverageRating = Review.averageRating(rs)
    }

    @Before
    fun initIntents() = Intents.init()

    @Before
    fun setupMocks() {
        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            (invocation.arguments[0] as FirestoreReviewService).also { service = it }
            mockListenerRegistration
        }

        val mockDocumentList = setupMockDocumentList()
        Mockito.`when`(mockSnapshot.documents).thenReturn(mockDocumentList)

        // Inject dependency
        FirestoreReviewService.databaseProvider = { mockFirestore }
    }

    private fun setupMockDocumentList(): List<DocumentSnapshot> {
        val ls: MutableList<DocumentSnapshot> = ArrayList()
        for (i in 1..10) {
            val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
            val review = Review(i.toFloat() / 2, TEST_TITLE, TEST_SUMMARY)
            Mockito.`when`(mockDocumentSnapshot.id).thenReturn(i.toString())
            Mockito.`when`(mockDocumentSnapshot.toObject(Review::class.java)).thenReturn(review)
            ls.add(mockDocumentSnapshot)
        }
        return ls
    }

    @Test
    fun testReviewsActivity() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), ReviewsActivity::class.java)
                .putExtra("POI_KEY", TEST_POI_KEY)

        val scenario: ActivityScenario<ReviewsActivity> = launch(intent)
        scenario.onActivity { _ ->
            service.onEvent(mockSnapshot, null)
            Log.e(TAG, "on event call from activity")
        }
    }

    @After
    fun releaseIntents() = Intents.release()
}