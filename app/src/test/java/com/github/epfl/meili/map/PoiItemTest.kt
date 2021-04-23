package com.github.epfl.meili.map

import com.github.epfl.meili.models.PointOfInterest
import org.junit.Assert.assertEquals
import org.junit.Test

class PoiItemTest {
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")

    @Test
    fun getPositionTest() {
        assertEquals(PoiItem(poi1).position, poi1.getLatLng())
        assertEquals(PoiItem(poi2).position, poi2.getLatLng())
    }

    @Test
    fun getTitleTest() {
        assertEquals(PoiItem(poi1).title, poi1.name)
        assertEquals(PoiItem(poi2).title, poi2.name)
    }

    @Test
    fun getSnippetTest() {
        assertEquals(PoiItem(poi1).snippet, null)
        assertEquals(PoiItem(poi2).snippet, null)
    }
}