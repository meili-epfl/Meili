package com.github.epfl.meili.poi


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.epfl.meili.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@LargeTest
@RunWith(AndroidJUnit4::class)
class PoiActivityTest {
    private val fake_poi: PointOfInterest =
        PointOfInterest(LatLng(10.0, 10.0), "ChIJAAAAAAAAAAARg4pb6XR5bo0", "art_brut")

    private val mockPlaces: PlacesClientService = Mockito.mock(PlacesClientService::class.java)
    private val mockPlacesClient: PlacesClient = Mockito.mock(PlacesClient::class.java)

    @get:Rule
    val mActivityTestRule: ActivityTestRule<PoiActivity> =
        object : ActivityTestRule<PoiActivity>(PoiActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
                return Intent(targetContext, PoiActivity::class.java).apply {
                    putExtra("POI_KEY", fake_poi)
                }
            }
        }

    @Before
    fun setup() {
        val placeBuilder = Place.builder()
        placeBuilder.address = "mockAddress"
        placeBuilder.phoneNumber = "mockPhone"
        placeBuilder.websiteUri = Uri.EMPTY
        placeBuilder.utcOffsetMinutes = 30
        placeBuilder.openingHours = OpeningHours.builder().build()
        placeBuilder.photoMetadatas = listOf(PhotoMetadata.builder("a").build())
        val place = placeBuilder.build()
        val fpr: FetchPlaceResponse = FetchPlaceResponse.newInstance(place)
        val tcs: TaskCompletionSource<FetchPlaceResponse> = TaskCompletionSource()
        tcs.setResult(fpr)

        `when`(mockPlaces.getPlacesClient(any(), any())).thenReturn(mockPlacesClient)
        `when`(mockPlacesClient.fetchPlace(any())).thenReturn(tcs.task)


        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8)
        val fpr2: FetchPhotoResponse = FetchPhotoResponse.newInstance(bitmap)
        val tcs2: TaskCompletionSource<FetchPhotoResponse> = TaskCompletionSource()
        tcs2.setResult(fpr2)

        `when`(mockPlacesClient.fetchPhoto(any())).thenReturn(tcs2.task)

        PoiInfoFragment.placesClientService = { mockPlaces }
    }

    @Test
    fun poiActivityTest() {
        onView(withId(R.id.pager)).perform(swipeLeft())
        onView(withId(R.id.pager)).perform(swipeLeft())
        pressBack()
        pressBack()
    }
}
