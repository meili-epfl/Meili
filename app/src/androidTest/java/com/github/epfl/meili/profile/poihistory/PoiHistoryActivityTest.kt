package com.github.epfl.meili.profile.poihistory

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
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.VisitedPointOfInterest
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class PoiHistoryActivityTest {

    companion object {
        private const val TEST_UID = "UID"
        private val TEST_POI =
            VisitedPointOfInterest(PointOfInterest(100.0, 100.0, "lorem_ipsum1", "lorem_ipsum2"))
    }

    private val mockFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = mock(DocumentReference::class.java)

    private val mockSnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    private lateinit var database: FirestoreDatabase<VisitedPointOfInterest>


    @get:Rule
    var rule: ActivityScenarioRule<PoiHistoryActivity> =
        ActivityScenarioRule(PoiHistoryActivity::class.java)

    @Before
    fun initIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    init {
        setupMocks()
        mockAuthenticationService.signInIntent()
    }

    private fun setupMocks() {
        `when`(mockFirestore.collection("poi-history/$TEST_UID/poi-history")).thenReturn(
            mockCollection
        )

        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as FirestoreDatabase<VisitedPointOfInterest>
            mock(ListenerRegistration::class.java)
        }
        `when`(mockCollection.document(contains(TEST_UID))).thenReturn(mockDocument)

        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.id).thenReturn(TEST_POI.poi?.uid)
        `when`(mockDocumentSnapshot.toObject(VisitedPointOfInterest::class.java)).thenReturn(
            TEST_POI
        )
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))


        mockAuthenticationService.setMockUid(TEST_UID)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        FirebaseStorageService.storageProvider = { mock(FirebaseStorage::class.java) }
        Auth.authService = mockAuthenticationService

    }

    @Test
    fun launchForumIntentsTest() {
        database.onEvent(mockSnapshot, null)


        onView(withId(R.id.poi_history_recycler_view))
            .check(matches(isDisplayed()))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(
                        withText(
                            TEST_POI.poi?.name
                        )
                    )
                )
            )

        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        onView(withText(TEST_POI.poi?.name)).perform(click())

        Intents.intended(allOf())


    }
}