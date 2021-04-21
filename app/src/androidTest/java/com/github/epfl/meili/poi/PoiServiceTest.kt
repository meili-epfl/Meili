package com.github.epfl.meili.poi

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PoiServiceTest {
    val poiService = PoiService()
    val expectedList = ArrayList<PointOfInterest>()
    val json: JSONObject
    val poi1 = PointOfInterest(-33.870775, 151.199025, "Rhythmboat Cruises", "ChIJyWEHuEmuEmsRm9hTkapTCrk", "http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png")
    val poi2 = PointOfInterest(-33.867591, 151.201196, "Australian Cruise Group", "ChIJrTLr-GyuEmsRBfy61i59si0", "http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png")

    init {
        json = getJsonDataFromAsset("poi-search-response.json")
    }

    @Test
    fun customOnSuccessFromTest() {
        poiService.customOnSuccessFrom {
            assertEquals(it[0], poi1)
            assertEquals(it[it.size - 1], poi2)
            assertEquals(it[it.size - 1].openNow, true)
        }(json)
    }

    @Test
    fun requestPoisTest() {
        val latLng = LatLng(23.0, 12.0)
        val mockQueue = Mockito.mock(RequestQueue::class.java)

        val onSuccess: (List<PointOfInterest>) -> Unit = {
            assertEquals(it[0], poi1)
            assertEquals(it[it.size - 1], poi2)
            assertEquals(it[it.size - 1].openNow, true)
        }

        Mockito.`when`(mockQueue.add(Mockito.any(JsonObjectRequest::class.java))).then {
            poiService.customOnSuccessFrom(onSuccess)(json)
            return@then null
        }

        poiService.setQueue(mockQueue)
        poiService.requestPois(latLng, onSuccess, { assert(false) })
    }

    @Test
    fun getReachablePoiTest() {
        val userPos = LatLng(41.075534, 1.131070)
        val poiList = ArrayList<PointOfInterest>()
        val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
        val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")
        poiList.add(poi1)
        poiList.add(poi2)

        expectedList.clear()
        expectedList.add(poi1)

        assertEquals(expectedList, PoiService().getReachablePoi(userPos, poiList, 75.0))
    }

    private fun getJsonDataFromAsset(fileName: String): JSONObject {
        val jsonString: String

        jsonString = getInstrumentation().context.assets.open(fileName).bufferedReader().use { it.readText() }

        return JSONObject(jsonString)
    }
}