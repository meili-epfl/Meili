package com.github.epfl.meili.review

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.Review
import com.google.firebase.firestore.*
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ReviewsActivityTest {

    companion object {
        private const val TAG = "ReviewsActivityTest"

        private const val TEST_POI_KEY = "poiKey"
        private const val TEST_TITLE = "Beach too sandy"
        private const val TEST_SUMMARY = "Water too wet"
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private lateinit var service: FirestoreReviewService
    private val mockSnapshot: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockListenerRegistration: ListenerRegistration = Mockito.mock(ListenerRegistration::class.java)

    private val mockAuthenticationService: AuthenticationService = MockAuthenticationService()

    private var testAverageRating: Float = 0f

    init {
        setupMocks()
    }

    private fun setupMocks() {
        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            (invocation.arguments[0] as FirestoreReviewService).also { service = it }
            mockListenerRegistration
        }

        val mockDocumentList = setupMockDocumentListAndAverageRating()
        Mockito.`when`(mockSnapshot.documents).thenReturn(mockDocumentList)

        // Inject dependencies
        FirestoreReviewService.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }

    private fun setupMockDocumentListAndAverageRating(): List<DocumentSnapshot> {
        val reviews: MutableMap<String, Review> = HashMap() // to compute average rating
        val documentList: MutableList<DocumentSnapshot> = ArrayList()

        for (i in 1..10) {
            val review = Review(i.toFloat() / 2, TEST_TITLE, TEST_SUMMARY)

            reviews[i.toString()] = review

            val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
            Mockito.`when`(mockDocumentSnapshot.id).thenReturn(i.toString())
            Mockito.`when`(mockDocumentSnapshot.toObject(Review::class.java)).thenReturn(review)
            documentList.add(mockDocumentSnapshot)
        }

        testAverageRating = Review.averageRating(reviews)
        return documentList
    }

    private val intent = Intent(getInstrumentation().targetContext.applicationContext, ReviewsActivity::class.java)
                            .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ReviewsActivity> = ActivityScenarioRule(intent)

    @Test
    fun testReviewsActivity() {
        service.onEvent(mockSnapshot, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.average_rating)).check(matches(hasTextEqualTo(testAverageRating.toString())))

        onView(withId(R.id.fab)).check(matches(isNotEnabled()))
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())))

        mockAuthenticationService.signInIntent()
        service.onEvent(mockSnapshot, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.average_rating)).check(matches(hasTextEqualTo(testAverageRating.toString())))

        onView(withId(R.id.fab)).perform(click())

        onView(withId(R.id.list_reviews)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_review)).check(matches(isDisplayed()))

        onView(withId(R.id.cancel_review)).perform(click())

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))
    }

    private fun hasTextEqualTo(content: String): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("TextView has the value: $content")
            }

            override fun matchesSafely(view: View?): Boolean {
                when (view) {
                    is TextView -> {
                        return view.text == content
                    }
                }
                return false
            }
        }
    }
}