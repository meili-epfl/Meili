package com.github.epfl.meili.poi

import com.google.android.gms.maps.model.LatLng
import junit.framework.Assert.assertEquals
import org.junit.Test

class PointOfInterestTest {
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")

    @Test
    fun toStringTest(){
        assertEquals("{POI:lat/lng: (41.075,1.13087),name:place1,uid:place1}", poi1.toString())
    }

    @Test
    fun equalsTest(){
        assertEquals(poi1.equals(PointOfInterest(41.075000, 1.130870, "place1", "place1")), true)
        assertEquals(poi1.equals(poi2), false)
        assertEquals(poi1.equals(poi1), true)
        assertEquals(poi1.equals(ArrayList<PointOfInterest>()), false)
    }
}