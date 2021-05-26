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
    fun equalsTest(){
        assertEquals(PoiItem(poi1).equals(null), false)
        assertEquals(PoiItem(poi1) == PoiItem(poi2), false)
        assertEquals(PoiItem(poi1) == PoiItem(poi1), true)
    }

    @Test
    fun hashCodeTest(){
        assertEquals(PoiItem(poi1).hashCode(), PoiItem(poi1).hashCode())
        assertEquals(PoiItem(poi1).hashCode()==PoiItem(poi2).hashCode(), false)
    }

    @Test
    fun toStringTest(){
        assertEquals(PoiItem(poi1).toString(), "PoiItem: $poi1")
    }
}