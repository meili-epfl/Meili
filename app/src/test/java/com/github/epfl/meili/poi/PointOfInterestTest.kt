package com.github.epfl.meili.poi

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
<<<<<<< HEAD

=======
>>>>>>> 8edff669cec91d93862df81ac0d59c6bec5245a5
import org.junit.Test

class PointOfInterestTest {
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")

    @Test
    fun toStringTest() {
        assertEquals("{POI:lat:41.075long:1.13087,name:place1,uid:place1}", poi1.toString())
    }

    @Test
    fun equalsTest() {
        assertEquals(poi1.equals(PointOfInterest(41.075000, 1.130870, "place1", "place1")), true)
        assertEquals(poi1.equals(poi2), false)
        assertEquals(poi1.equals(poi1), true)
        assertEquals(poi1.equals(ArrayList<PointOfInterest>()), false)
    }

    @Test
    fun getLatLngTest() {
        assertEquals(poi1.getLatLng(), LatLng(poi1.latitude, poi1.longitude))
    }
}