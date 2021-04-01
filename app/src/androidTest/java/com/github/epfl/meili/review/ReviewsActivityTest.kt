package com.github.epfl.meili.review

import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
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
import org.mockito.Mockito.`when`


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ReviewsActivityTest {

    companion object {
        private const val TAG = "ReviewsActivityTest"

        private const val TEST_UID = "MrPerfect"
        private const val TEST_POI_KEY = "poiKey"
        private const val TEST_TITLE = "Beach too sandy"
        private const val TEST_SUMMARY = "Water too wet"

        private const val AVERAGE_FORMAT = "%.2f"

        private const val NUM_REVIEWS_BEFORE_ADDITION = 10

        private const val ADDED_REVIEW_RATING = 0.5f
        private const val TEST_ADDED_TITLE = "Desert too sandy"

        private const val EDITED_REVIEW_RATING = 5f
        private const val TEST_EDITED_TITLE = "Looks good to me"
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)
    private val mockListenerRegistration: ListenerRegistration = Mockito.mock(ListenerRegistration::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterEdition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()
    private lateinit var service: FirestoreReviewService

    private var testAverageRatingBeforeAddition: Float = 0f
    private var testAverageRatingAfterAddition: Float = 0f
    private var testAverageRatingAfterEdition: Float = 0f

    init {
        setupMocks()
    }

    private fun setupMocks() {
        mockAuthenticationService.setMockUid(TEST_UID)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            (invocation.arguments[0] as FirestoreReviewService).also { service = it }
            mockListenerRegistration
        }
        `when`(mockCollection.document(Mockito.matches(TEST_UID))).thenReturn(mockDocument)

        var mockDocumentList = beforeAdditionList()
        `when`(mockSnapshotBeforeAddition.documents).thenReturn(mockDocumentList)

        val mockDocumentListAfterAddition = ArrayList(mockDocumentList)
        mockDocumentListAfterAddition.add(addedReviewDocumentSnapshot())
        `when`(mockSnapshotAfterAddition.documents).thenReturn(mockDocumentListAfterAddition)

        val mockDocumentListAfterEdition = ArrayList(mockDocumentList)
        mockDocumentListAfterEdition.add(editedReviewDocumentSnapshot())
        `when`(mockSnapshotAfterEdition.documents).thenReturn(mockDocumentListAfterEdition)

        // Inject dependencies
        FirestoreReviewService.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }

    private fun editedReviewDocumentSnapshot(): DocumentSnapshot {
        testAverageRatingAfterEdition = (NUM_REVIEWS_BEFORE_ADDITION * testAverageRatingBeforeAddition + EDITED_REVIEW_RATING) /
                (NUM_REVIEWS_BEFORE_ADDITION + 1)
        return getMockDocumentSnapshot(TEST_UID, Review(EDITED_REVIEW_RATING, TEST_EDITED_TITLE, TEST_SUMMARY))
    }

    private fun addedReviewDocumentSnapshot(): DocumentSnapshot {
        testAverageRatingAfterAddition = (NUM_REVIEWS_BEFORE_ADDITION * testAverageRatingBeforeAddition + ADDED_REVIEW_RATING) /
                (NUM_REVIEWS_BEFORE_ADDITION + 1)
        return getMockDocumentSnapshot(TEST_UID, Review(ADDED_REVIEW_RATING, TEST_ADDED_TITLE, TEST_SUMMARY))
    }

    private fun beforeAdditionList(): MutableList<DocumentSnapshot> {
        val reviews: MutableMap<String, Review> = HashMap() // to compute average rating
        val documentList: MutableList<DocumentSnapshot> = ArrayList()

        for (i in 1..NUM_REVIEWS_BEFORE_ADDITION) {
            val review = Review(i.toFloat() / 2, TEST_TITLE, TEST_SUMMARY)
            reviews[i.toString()] = review
            documentList.add(getMockDocumentSnapshot(i.toString(), review))
        }

        testAverageRatingBeforeAddition = Review.averageRating(reviews)
        return documentList
    }

    private fun getMockDocumentSnapshot(id: String, review: Review): DocumentSnapshot {
        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.id).thenReturn(id)
        `when`(mockDocumentSnapshot.toObject(Review::class.java)).thenReturn(review)
        return mockDocumentSnapshot
    }

    private val intent = Intent(getInstrumentation().targetContext.applicationContext, ReviewsActivity::class.java)
                            .putExtra("POI_KEY", TEST_POI_KEY)

    @get:Rule
    var rule: ActivityScenarioRule<ReviewsActivity> = ActivityScenarioRule(intent)

    @Test
    fun signedOutDisplayTest() {
        mockAuthenticationService.signOut()
        service.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.average_rating)).check(matches(textViewContainsText(AVERAGE_FORMAT.format(testAverageRatingBeforeAddition))))

        onView(withId(R.id.fab_add_edit_review)).check(matches(isNotEnabled()))
        onView(withId(R.id.fab_add_edit_review)).check(matches(not(isDisplayed())))
    }

    @Test
    fun signedInDisplayTest() {
        mockAuthenticationService.signInIntent()
        service.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.average_rating)).check(matches(textViewContainsText(AVERAGE_FORMAT.format(testAverageRatingBeforeAddition))))

        onView(withId(R.id.fab_add_edit_review)).check(matches(isEnabled()))
        onView(withId(R.id.fab_add_edit_review)).check(matches(isDisplayed()))
    }

    @Test
    fun signedInCancelAddingTest() {
        mockAuthenticationService.signInIntent()
        service.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.fab_add_edit_review)).perform(click())

        onView(withId(R.id.list_reviews)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_review)).check(matches(isDisplayed()))

        onView(withId(R.id.cancel_review)).perform(click())

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withText(TEST_ADDED_TITLE)).check(doesNotExist())
    }

    @Test
    fun signedInAddReviewTest() {
        mockAuthenticationService.signInIntent()
        service.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.fab_add_edit_review)).perform(click())

        onView(withId(R.id.rating_bar)).perform(setRating(ADDED_REVIEW_RATING))
        onView(withId(R.id.edit_title)).perform(clearText(), typeText(TEST_ADDED_TITLE), closeSoftKeyboard())
        onView(withId(R.id.edit_summary)).perform(clearText(), typeText(TEST_SUMMARY), closeSoftKeyboard())

        onView(withId(R.id.submit_review)).perform(click())

        // send reviews map with added review to review service
        service.onEvent(mockSnapshotAfterAddition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recycler_view))
                .check(matches(isDisplayed()))
                .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(TEST_UID))))

        onView(withText(TEST_UID)).check(matches(isDisplayed()))
        onView(textViewContainsText(TEST_ADDED_TITLE)).check(matches(isDisplayed()))

        onView(withId(R.id.average_rating)).check(matches(textViewContainsText(AVERAGE_FORMAT.format(testAverageRatingAfterAddition))))
    }

    @Test
    fun signedInEditReviewTest() {
        mockAuthenticationService.signInIntent()
        service.onEvent(mockSnapshotAfterAddition, null) // mock user has existing review

        onView(withId(R.id.fab_add_edit_review)).perform(click())

        // old review is displayed
        onView(withId(R.id.edit_title)).check(matches(editTextContainsText(TEST_ADDED_TITLE)))
        onView(withId(R.id.edit_summary)).check(matches(editTextContainsText(TEST_SUMMARY)))
        onView(withId(R.id.rating_bar)).check(matches(ratingBarHasRating(ADDED_REVIEW_RATING)))

        // edit review
        onView(withId(R.id.rating_bar)).perform(setRating(EDITED_REVIEW_RATING))
        onView(withId(R.id.edit_title)).perform(clearText(), typeText(TEST_EDITED_TITLE), closeSoftKeyboard())
        onView(withId(R.id.submit_review)).perform(click())

        // send reviews map with edited review to review service
        service.onEvent(mockSnapshotAfterEdition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recycler_view))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(TEST_UID))))

        onView(withText(TEST_UID)).check(matches(isDisplayed()))
        onView(textViewContainsText(TEST_EDITED_TITLE)).check(matches(isDisplayed()))

        onView(withText(TEST_ADDED_TITLE)).check(doesNotExist())

        onView(withId(R.id.average_rating)).check(matches(textViewContainsText(AVERAGE_FORMAT.format(testAverageRatingAfterEdition))))
    }

    private fun ratingBarHasRating(rating: Float): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("A RatingBar with the value: $rating")
            }

            override fun matchesSafely(item: View?): Boolean {
                when (item) {
                    is RatingBar -> return item.rating == rating
                }
                return false
            }
        }
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

    private fun editTextContainsText(content: String): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("An EditText with the text: $content")
            }

            override fun matchesSafely(item: View?): Boolean {
                when (item) {
                    is EditText -> return item.text.toString() == content
                }
                return false
            }
        }
    }

    private fun setRating(rating: Float): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(RatingBar::class.java)
            }

            override fun getDescription(): String {
                return "Custom view action to set rating bar's value to $rating"
            }

            override fun perform(uiController: UiController?, view: View) {
                (view as RatingBar).also { it.rating = rating }
            }
        }
    }
}