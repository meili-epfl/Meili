package com.github.epfl.meili.profile.favoritepois

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase

import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiActivity
import com.github.epfl.meili.posts.forum.ForumActivity
import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.firebase.firestore.*
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class FavoritePoisActivityTest {

    companion object {
        private const val TEST_UID = "UID"
        private val TEST_POI =
            PointOfInterest(100.0, 100.0, "lorem_ipsum1", "lorem_ipsum2")
    }

    private val mockFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = mock(DocumentReference::class.java)

    private val mockSnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    private lateinit var database: FirestoreDatabase<PointOfInterest>


    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        FavoritePoisActivity::class.java
    )
        .putExtra(ProfileActivity.USER_KEY, TEST_UID)

    @get:Rule
    var rule: ActivityScenarioRule<FavoritePoisActivity> = ActivityScenarioRule(intent)

    @Before
    fun initIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    init {
        setupMocks()
        mockAuthenticationService.signInIntent(null)
    }

    private fun setupMocks() {
        `when`(mockFirestore.collection("poi-favorite/$TEST_UID/poi-favorite")).thenReturn(
            mockCollection
        )

        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<PointOfInterest>
            mock(ListenerRegistration::class.java)
        }
        `when`(mockCollection.document(ArgumentMatchers.matches(TEST_POI.uid))).thenReturn(
            mockDocument
        )

        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.id).thenReturn(TEST_POI.uid)
        `when`(mockDocumentSnapshot.toObject(PointOfInterest::class.java)).thenReturn(
            TEST_POI
        )
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))


        mockAuthenticationService.setMockUid(TEST_UID)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService

    }

    @Test
    fun launchForumIntentsTest() {
        database.onEvent(mockSnapshot, null)


        onView(withId(R.id.favorite_pois_recycler_view))
            .check(matches(isDisplayed()))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(
                        withText(
                            TEST_POI.name
                        )
                    )
                )
            )

        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        onView(withText(TEST_POI.name)).perform(click())

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasComponent(PoiActivity::class.java.name)
            )
        )


    }
}