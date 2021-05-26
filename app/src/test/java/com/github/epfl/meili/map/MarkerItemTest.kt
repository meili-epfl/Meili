package com.github.epfl.meili.map

import com.github.epfl.meili.models.PointOfInterest
import org.junit.Assert.assertEquals
import org.junit.Test

class MarkerItemTest {
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")

    @Test
    fun getPositionTest() {
        assertEquals(MarkerItem(poi1).position, poi1.getLatLng())
        assertEquals(MarkerItem(poi2).position, poi2.getLatLng())
    }

    @Test
    fun getTitleTest() {
        assertEquals(MarkerItem(poi1).title, poi1.name)
        assertEquals(MarkerItem(poi2).title, poi2.name)
    }

    @Test
    fun getSnippetTest() {
        assertEquals(MarkerItem(poi1).snippet, null)
        assertEquals(MarkerItem(poi2).snippet, null)
    }

    @Test
    fun equalsTest() {
        assertEquals(MarkerItem(poi1).equals(null), false)
        assertEquals(MarkerItem(poi1) == MarkerItem(poi2), false)
        assertEquals(MarkerItem(poi1) == MarkerItem(poi1), true)
    }

    @Test
    fun hashCodeTest() {
        assertEquals(MarkerItem(poi1).hashCode(), MarkerItem(poi1).hashCode())
        assertEquals(MarkerItem(poi1).hashCode() == MarkerItem(poi2).hashCode(), false)
    }

    @Test
    fun toStringTest() {
        assertEquals(MarkerItem(poi1).toString(), "PoiItem: $poi1")
    }
}