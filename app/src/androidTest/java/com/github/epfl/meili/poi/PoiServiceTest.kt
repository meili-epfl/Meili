package com.github.epfl.meili.poi

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PoiServiceTest {
    val poiService = PoiService()
    val expectedList = ArrayList<PointOfInterest>()
    val json = JSONObject(
            "{\"elements\":[{\"id\":1234,\"lat\":12,\"lon\":34,\"tags\":{\"name\":\"Monument a Jaume I\"}}, {\"id\":1234,\"lat\":12,\"lon\":34}]}"
    )

    @Before
    fun initList() {
        val expectedPoi = PointOfInterest(LatLng(12.0, 34.0), "Monument a Jaume I", "1234")
        expectedList.add(expectedPoi)
    }

    @Test
    fun customOnSuccessFromTest() {

        poiService.customOnSuccessFrom { assertEquals(expectedList, it) }(json)
    }

    @Test
    fun requestPoisTest() {
        val latLng = LatLng(23.0, 12.0)
        val mockQueue = Mockito.mock(RequestQueue::class.java)

        val onSuccess: (List<PointOfInterest>) -> Unit = { it -> assertEquals(expectedList, it) }

        Mockito.`when`(mockQueue.add(Mockito.any(JsonObjectRequest::class.java))).then {
            poiService.customOnSuccessFrom(onSuccess)(json)
            return@then null
        }

        poiService.setQueue(mockQueue)
        poiService.requestPois(latLng, onSuccess, { assert(false) })
    }
}