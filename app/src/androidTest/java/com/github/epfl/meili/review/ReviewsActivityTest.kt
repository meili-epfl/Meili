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
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.friends.UserInfoService
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.firebase.firestore.*
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ReviewsActivityTest {

    companion object {
        private const val TEST_AUTHOR_USERNAME = "MrPerfect"
        private const val TEST_OTHER_AUTHOR_USERNAME = "MrPerfect2"
        private const val TEST_REVIEW_ID = "test uid"

        private const val TEST_POI_KEY = "lorem_ipsum2"
        private val TEST_POI = PointOfInterest(100.0, 100.0, "lorem_ipsum1", TEST_POI_KEY)
        private const val TEST_TITLE = "Beach too sandy"
        private const val TEST_SUMMARY = "Water too wet"
        private const val TEST_AUTHOR_ID = "author id"
        private const val TEST_OTHER_AUTHOR_ID = "author id2"

        private const val AVERAGE_FORMAT = "%.2f"

        private const val NUM_REVIEWS_BEFORE_ADDITION = 10

        private const val ADDED_REVIEW_RATING = 0.5f
        private const val TEST_ADDED_TITLE = "Desert too sandy"

        private const val EDITED_REVIEW_RATING = 5f
        private const val TEST_EDITED_TITLE = "Looks good to me"
    }

    private val mockFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterEdition: QuerySnapshot = mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()
    private val mockUserInfoService = Mockito.mock(UserInfoService::class.java)
    private lateinit var database: FirestoreDatabase<Review>

    private var testAverageRatingBeforeAddition: Float = 0f
    private var testAverageRatingAfterAddition: Float = 0f
    private var testAverageRatingAfterEdition: Float = 0f

    init {
        setupMocks()
    }

    @Before
    fun startUserInfoService() {
        val testFriendMap = HashMap<String, User>()
        testFriendMap[TEST_AUTHOR_ID] = User(TEST_AUTHOR_ID, TEST_AUTHOR_USERNAME)
        testFriendMap[TEST_OTHER_AUTHOR_ID] = User(TEST_OTHER_AUTHOR_ID, TEST_OTHER_AUTHOR_USERNAME)

        `when`(
                mockUserInfoService.getUserInformation(
                        Mockito.anyList(),
                        Mockito.any(),
                        Mockito.any()
                )
        ).then {
            val onSuccess = it.arguments[1] as ((Map<String, User>) -> Unit)

            onSuccess(testFriendMap)

            return@then null
        }
        ReviewsActivity.serviceProvider = { mockUserInfoService }
    }

    private fun setupMocks() {
        `when`(mockFirestore.collection("reviews")).thenReturn(mockCollection)
        val mockQuery = mock(Query::class.java)
        `when`(
                mockCollection.whereEqualTo(
                        Review.POI_KEY_FIELD,
                        TEST_POI_KEY
                )
        ).thenReturn(mockQuery)
        `when`(mockQuery.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<Review>
            mock(ListenerRegistration::class.java)
        }
        `when`(mockCollection.document(ArgumentMatchers.matches(TEST_REVIEW_ID))).thenReturn(
                mockDocument
        )

        val mockDocumentList = beforeAdditionList()
        `when`(mockSnapshotBeforeAddition.documents).thenReturn(mockDocumentList)

        val mockDocumentListAfterAddition = ArrayList(mockDocumentList)
        mockDocumentListAfterAddition.add(addedReviewDocumentSnapshot())
        `when`(mockSnapshotAfterAddition.documents).thenReturn(mockDocumentListAfterAddition)

        val mockDocumentListAfterEdition = ArrayList(mockDocumentList)
        mockDocumentListAfterEdition.add(editedReviewDocumentSnapshot())
        `when`(mockSnapshotAfterEdition.documents).thenReturn(mockDocumentListAfterEdition)

        mockAuthenticationService.setMockUid(TEST_REVIEW_ID)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }

    private fun editedReviewDocumentSnapshot(): DocumentSnapshot {
        testAverageRatingAfterEdition =
                (NUM_REVIEWS_BEFORE_ADDITION * testAverageRatingBeforeAddition + EDITED_REVIEW_RATING) /
                        (NUM_REVIEWS_BEFORE_ADDITION + 1)
        return getMockDocumentSnapshot(
                TEST_REVIEW_ID,
                Review(
                        TEST_OTHER_AUTHOR_ID,
                        TEST_POI_KEY,
                        EDITED_REVIEW_RATING,
                        TEST_EDITED_TITLE,
                        TEST_SUMMARY
                )
        )
    }

    private fun addedReviewDocumentSnapshot(): DocumentSnapshot {
        testAverageRatingAfterAddition =
                (NUM_REVIEWS_BEFORE_ADDITION * testAverageRatingBeforeAddition + ADDED_REVIEW_RATING) /
                        (NUM_REVIEWS_BEFORE_ADDITION + 1)
        return getMockDocumentSnapshot(
                TEST_REVIEW_ID,
                Review(
                        TEST_OTHER_AUTHOR_ID,
                        TEST_POI_KEY,
                        ADDED_REVIEW_RATING,
                        TEST_ADDED_TITLE,
                        TEST_SUMMARY
                )
        )
    }

    private fun beforeAdditionList(): MutableList<DocumentSnapshot> {
        val reviews: MutableMap<String, Review> = HashMap() // to compute average rating
        val documentList: MutableList<DocumentSnapshot> = ArrayList()

        for (i in 1..NUM_REVIEWS_BEFORE_ADDITION) {
            val review =
                    Review(
                            TEST_AUTHOR_ID,
                            TEST_POI_KEY,
                            i.toFloat() / 2,
                            TEST_TITLE,
                            TEST_SUMMARY
                    )
            reviews[i.toString()] = review
            documentList.add(getMockDocumentSnapshot(i.toString(), review))
        }

        testAverageRatingBeforeAddition = Review.averageRating(reviews)
        return documentList
    }

    private fun getMockDocumentSnapshot(id: String, review: Review): DocumentSnapshot {
        val mockDocumentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.id).thenReturn(id)
        `when`(mockDocumentSnapshot.toObject(Review::class.java)).thenReturn(review)
        return mockDocumentSnapshot
    }

    private val intent =
            Intent(getInstrumentation().targetContext.applicationContext, ReviewsActivity::class.java)
                    .putExtra("POI_KEY", TEST_POI)

    @get:Rule
    var rule: ActivityScenarioRule<ReviewsActivity> = ActivityScenarioRule(intent)

    @Test
    fun signedOutDisplayTest() {
        mockAuthenticationService.signOut()
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.average_rating)).check(
                matches(
                        textViewContainsText(
                                AVERAGE_FORMAT.format(
                                        testAverageRatingBeforeAddition
                                )
                        )
                )
        )

        onView(withId(R.id.fab_add_edit_review)).check(matches(isNotEnabled()))
        onView(withId(R.id.fab_add_edit_review)).check(matches(not(isDisplayed())))
    }

    @Test
    fun signedInDisplayTest() {
        mockAuthenticationService.signInIntent(null)
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.average_rating)).check(
                matches(
                        textViewContainsText(
                                AVERAGE_FORMAT.format(
                                        testAverageRatingBeforeAddition
                                )
                        )
                )
        )

        onView(withId(R.id.fab_add_edit_review)).check(matches(isEnabled()))
        onView(withId(R.id.fab_add_edit_review)).check(matches(isDisplayed()))
    }

    @Test
    fun signedInCancelAddingTest() {
        mockAuthenticationService.signInIntent(null)
        database.onEvent(mockSnapshotBeforeAddition, null)

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
        mockAuthenticationService.signInIntent(null)
        database.onEvent(mockSnapshotBeforeAddition, null)

        onView(withId(R.id.fab_add_edit_review)).perform(click())

        onView(withId(R.id.rating_bar)).perform(setRating(ADDED_REVIEW_RATING))
        onView(withId(R.id.review_edit_title)).perform(
                clearText(),
                typeText(TEST_ADDED_TITLE),
                closeSoftKeyboard()
        )
        onView(withId(R.id.review_edit_summary)).perform(
                clearText(),
                typeText(TEST_SUMMARY),
                closeSoftKeyboard()
        )

        onView(withId(R.id.submit_review)).perform(click())

        // send reviews map with added review to review service
        database.onEvent(mockSnapshotAfterAddition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.reviews_recycler_view))
                .check(matches(isDisplayed()))
                .perform(
                        RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                                hasDescendant(
                                        withText(
                                                TEST_OTHER_AUTHOR_USERNAME
                                        )
                                )
                        )
                )

        onView(textViewContainsText(TEST_OTHER_AUTHOR_USERNAME)).check(matches(isDisplayed()))
        onView(textViewContainsText(TEST_ADDED_TITLE)).check(matches(isDisplayed()))

        onView(withId(R.id.average_rating)).check(
                matches(
                        textViewContainsText(
                                AVERAGE_FORMAT.format(
                                        testAverageRatingAfterAddition
                                )
                        )
                )
        )
    }

    @Test
    fun signedInEditReviewTest() {
        mockAuthenticationService.signInIntent(null)
        database.onEvent(mockSnapshotAfterAddition, null) // mock user has existing review

        onView(withId(R.id.fab_add_edit_review)).perform(click())

        // old review is displayed
        onView(withId(R.id.review_edit_title)).check(matches(editTextContainsText(TEST_ADDED_TITLE)))
        onView(withId(R.id.review_edit_summary)).check(matches(editTextContainsText(TEST_SUMMARY)))
        onView(withId(R.id.rating_bar)).check(matches(ratingBarHasRating(ADDED_REVIEW_RATING)))

        // edit review
        onView(withId(R.id.rating_bar)).perform(setRating(EDITED_REVIEW_RATING))
        onView(withId(R.id.review_edit_title)).perform(
                clearText(),
                typeText(TEST_EDITED_TITLE),
                closeSoftKeyboard()
        )
        onView(withId(R.id.submit_review)).perform(click())

        // send reviews map with edited review to review service
        database.onEvent(mockSnapshotAfterEdition, null)

        onView(withId(R.id.list_reviews)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_review)).check(matches(not(isDisplayed())))

        onView(withId(R.id.reviews_recycler_view))
                .check(matches(isDisplayed()))
                .perform(
                        RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                                hasDescendant(
                                        withText(
                                                TEST_OTHER_AUTHOR_USERNAME
                                        )
                                )
                        )
                )

        onView(withText(TEST_OTHER_AUTHOR_USERNAME)).check(matches(isDisplayed()))
        onView(textViewContainsText(TEST_EDITED_TITLE)).check(matches(isDisplayed()))

        onView(withText(TEST_ADDED_TITLE)).check(doesNotExist())

        onView(withId(R.id.average_rating)).check(
                matches(
                        textViewContainsText(
                                AVERAGE_FORMAT.format(
                                        testAverageRatingAfterEdition
                                )
                        )
                )
        )
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