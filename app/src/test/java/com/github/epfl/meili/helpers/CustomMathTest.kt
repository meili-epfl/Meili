package com.github.epfl.meili.helpers

import com.google.android.gms.maps.model.LatLng
import junit.framework.Assert.assertEquals
import org.junit.Test

class CustomMathTest {
    @Test
    fun distanceOnSphereTest(){
        assertEquals( 4196, Math.round(CustomMath.distanceOnSphere(LatLng(41.063563,1.083658), LatLng(41.075534,1.131070))))
    }
}