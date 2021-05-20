package com.github.epfl.meili.models

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PointOfInterestTest {
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1", "icon_string")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")

    @Test
    fun toStringTest() {
        assertEquals("{POI:lat:41.075long:1.13087,name:place1,uid:place1,icon:icon_string}", poi1.toString())
    }

    @Test
    fun equalsTest() {
        assertEquals(poi1 == PointOfInterest(41.075000, 1.130870, "place1", "place1", "icon_string"), true)
        assertEquals(poi1 == poi2, false)
        assertEquals(poi1 == poi1, true)
        assertEquals(poi1.equals(ArrayList<PointOfInterest>()), false)
    }

    @Test
    fun getLatLngTest() {
        assertEquals(poi1.getLatLng(), LatLng(poi1.latitude, poi1.longitude))
    }

    @Test
    fun hashCodeTest() {
        assertNotEquals(poi1.hashCode(), poi2.hashCode())
    }
}