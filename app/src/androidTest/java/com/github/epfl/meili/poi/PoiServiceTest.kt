package com.github.epfl.meili.poi

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PoiServiceTest {
    val poiService: PoiService = PoiServiceCached()
    val expectedList = ArrayList<PointOfInterest>()

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

        assertEquals(expectedList, poiService.getReachablePoi(userPos, poiList, 75.0))
    }
}