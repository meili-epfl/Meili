package com.github.epfl.meili.map

import android.content.Context
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class MarkerRendererTest {
    @Test
    fun generalTest() {
        MapsInitializer.initialize(MainApplication.applicationContext())
        Looper.prepare()
        val mockClusterManager = Mockito.mock(ClusterManager::class.java)
        val renderer = MarkerRendererTester(
            MainApplication.applicationContext(),
            null,
            mockClusterManager as ClusterManager<MarkerItem>
        )


        val poi1 = MarkerItem(PointOfInterest(41.075000, 1.130870, "place1", "place1"))
        val poi2 = MarkerItem(PointOfInterest(41.063563, 1.083658, "place2", "place2"))

        val poiStatusMap = HashMap<MarkerItem, PointOfInterestStatus>()

        poiStatusMap[poi1] = PointOfInterestStatus.VISIBLE
        poiStatusMap[poi2] = PointOfInterestStatus.REACHABLE

        renderer.renderClusterItems(poiStatusMap)

        val markerOptions = MarkerOptions()

        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)
        assertEquals(markerOptions.icon, MarkerRenderer.VISIBLE_ICON)

        renderer.onBeforeClusterItemRenderedCaller(poi2, markerOptions)

        assertEquals(markerOptions.icon, MarkerRenderer.REACHABLE_ICON)

        poiStatusMap[poi1] = PointOfInterestStatus.VISITED
        renderer.renderClusterItems(poiStatusMap)
        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)

        assertEquals(markerOptions.icon, MarkerRenderer.VISITED_ICON)
    }
}


class MarkerRendererTester(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<MarkerItem>
) : MarkerRenderer(context, map, clusterManager) {
    fun onBeforeClusterItemRenderedCaller(item: MarkerItem, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}