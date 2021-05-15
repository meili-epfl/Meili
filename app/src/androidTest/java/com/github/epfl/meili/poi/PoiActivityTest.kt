package com.github.epfl.meili.poi


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@LargeTest
@RunWith(AndroidJUnit4::class)
class PoiActivityTest {
    private val fakePoi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "art_brut", "ChIJAAAAAAAAAAARg4pb6XR5bo0")

    private val mockPlaces: PlacesClientService = Mockito.mock(PlacesClientService::class.java)
    private val mockPlacesClient: PlacesClient = Mockito.mock(PlacesClient::class.java)


    init {
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

        `when`(mockPlacesClient.fetchPlace(any())).thenReturn(tcs.task)


        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8)
        val fpr2: FetchPhotoResponse = FetchPhotoResponse.newInstance(bitmap)
        val tcs2: TaskCompletionSource<FetchPhotoResponse> = TaskCompletionSource()
        tcs2.setResult(fpr2)

        `when`(mockPlacesClient.fetchPhoto(any())).thenReturn(tcs2.task)

        `when`(mockPlaces.getPlacesClient(MockitoHelper.anyObject(), MockitoHelper.anyObject())).thenReturn(mockPlacesClient)

        PoiInfoFragment.placesClientService = { mockPlaces }
    }


    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        PoiActivity::class.java
    ).putExtra("POI_KEY", fakePoi)

    @get:Rule
    var mActivityTestRule: ActivityScenarioRule<PoiActivity> = ActivityScenarioRule(intent)

    @Test
    fun poiActivityTest() {
        onView(withId(R.id.pager)).perform(swipeLeft())
        onView(withId(R.id.pager)).perform(swipeLeft())
        pressBack()
        pressBack()
    }

    object MockitoHelper {
        fun <T> anyObject(): T {
            Mockito.any<T>()
            return uninitialized()
        }
        @Suppress("UNCHECKED_CAST")
        fun <T> uninitialized(): T =  null as T
    }
}
