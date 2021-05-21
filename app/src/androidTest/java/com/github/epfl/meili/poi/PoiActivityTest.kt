package com.github.epfl.meili.poi

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@Suppress("UNCHECKED_CAST")
@LargeTest
@RunWith(AndroidJUnit4::class)
class PoiActivityTest {

    companion object {
        private const val TEST_USERNAME = "AUTHOR"
        private const val TEST_TIMESTAMP = -1L
        private const val TEST_AUTHOR_ID = "author id"
        private const val TEST_UID = TEST_AUTHOR_ID + TEST_TIMESTAMP.toString()
        private const val TEST_POI_KEY = "lorem_ipsum2"
    }


    private val fakePoi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "art_brut", "ChIJAAAAAAAAAAARg4pb6XR5bo0")

    private val mockPlaces: PlacesClientService = Mockito.mock(PlacesClientService::class.java)
    private val mockPlacesClient: PlacesClient = Mockito.mock(PlacesClient::class.java)
    private val mockPoiHistory: CollectionReference = Mockito.mock(CollectionReference::class.java)

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private lateinit var poiDatabase: FirestoreDatabase<PointOfInterest>
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)
    private val mockAuthenticationService = MockAuthenticationService()


    init {
        val placeBuilder = Place.builder()
        placeBuilder.address = "mockAddress"
        placeBuilder.phoneNumber = "mockPhone"
        placeBuilder.websiteUri = Uri.parse("http://epfl.ch")
        placeBuilder.utcOffsetMinutes = 30
        placeBuilder.openingHours = OpeningHours.builder().build()
        placeBuilder.photoMetadatas = listOf(PhotoMetadata.builder("a").build())
        val place = placeBuilder.build()
        val fpr: FetchPlaceResponse = FetchPlaceResponse.newInstance(place)
        val tcs: TaskCompletionSource<FetchPlaceResponse> = TaskCompletionSource()
        tcs.setResult(fpr)

        `when`(mockPlacesClient.fetchPlace(any())).thenReturn(tcs.task)


        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8)
        val fpr2: FetchPhotoResponse = FetchPhotoResponse.newInstance(bitmap)
        val tcs2: TaskCompletionSource<FetchPhotoResponse> = TaskCompletionSource()
        tcs2.setResult(fpr2)

        `when`(mockPlacesClient.fetchPhoto(any())).thenReturn(tcs2.task)

        `when`(mockPlaces.getPlacesClient(any(), any())).thenReturn(mockPlacesClient)

        // Mock poi history
        `when`(mockFirestore.collection("poi-history/${TEST_UID}/poi-history")).thenReturn(
            mockPoiHistory
        )
        `when`(mockPoiHistory.addSnapshotListener(any())).thenAnswer { invocation ->
            poiDatabase = invocation.arguments[0] as FirestoreDatabase<PointOfInterest>
            Mockito.mock(ListenerRegistration::class.java)
        }
        `when`(mockPoiHistory.document(ArgumentMatchers.matches(TEST_POI_KEY))).thenReturn(
            mockDocument
        )

        mockAuthenticationService.setMockUid(TEST_UID)
        mockAuthenticationService.setUsername(TEST_USERNAME)

        PoiActivity.placesClientService = { mockPlaces }
        FirestoreDatabase.databaseProvider = { mockFirestore }
        Auth.authService = mockAuthenticationService
    }


    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        PoiActivity::class.java
    ).putExtra("POI_KEY", fakePoi)

    @get:Rule
    var mActivityTestRule: ActivityScenarioRule<PoiActivity> = ActivityScenarioRule(intent)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun poiActivityTest() {
        onView(withId(R.id.poi_name)).check(matches(ViewMatchers.withText(fakePoi.name)))
        onView(withId(R.id.openStatusTextView)).check(matches(ViewMatchers.withText("CLOSED")))
        onView(withId(R.id.call_poi_button)).check(matches(isDisplayed()))
        onView(withId(R.id.take_me_there_button)).check(matches(isDisplayed()))
    }

    @Test
    fun takeMeThereButtonTest() {
        onView(withId(R.id.take_me_there_button)).perform(click())
        Intents.intended(hasPackage("com.google.android.apps.maps"))
    }

    @Test
    fun callButtonTest() {
        onView(withId(R.id.call_poi_button)).perform(click())
        Intents.intended(toPackage("com.android.server.telecom"))
    }
}

