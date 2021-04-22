package com.github.epfl.meili.poi

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.apps.common.testing.accessibility.framework.replacements.Point
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
        val expectedPoi = PointOfInterest(12.0, 34.0, "Monument a Jaume I", "1234")
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

    @Test
    fun getReachablePoiTest(){
        val userPos = LatLng(41.075534,1.131070)
        val poiList = ArrayList<PointOfInterest>()
        val poi1 = PointOfInterest(41.075000,1.130870, "place1", "place1")
        val poi2 = PointOfInterest(41.063563,1.083658, "place2", "place2")
        poiList.add(poi1)
        poiList.add(poi2)

        expectedList.clear()
        expectedList.add(poi1)

        assertEquals(expectedList, PoiService().getReachablePoi(userPos, poiList, 75.0))
    }
}