package com.github.epfl.meili.map

import android.content.Context
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainApplication
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PoiRendererTest {

    @Test
    fun generalTest() {
        MapsInitializer.initialize(MainApplication.applicationContext())
        Looper.prepare()
        val mockClusterManager = Mockito.mock(ClusterManager::class.java)
        val renderer = PoiRendererTester(MainApplication.applicationContext(), null, mockClusterManager as ClusterManager<PoiItem>)


        val poi1 = PoiItem(PointOfInterest(41.075000, 1.130870, "place1", "place1"))
        val poi2 = PoiItem(PointOfInterest(41.063563, 1.083658, "place2", "place2"))

        val poiStatusMap = HashMap<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>()

        poiStatusMap.put(poi1, PoiMarkerViewModel.PointOfInterestStatus.VISIBLE)
        poiStatusMap.put(poi2, PoiMarkerViewModel.PointOfInterestStatus.REACHABLE)

        renderer.renderClusterItems(poiStatusMap)

        val markerOptions = MarkerOptions()

        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)
        assertEquals(markerOptions.icon, PoiRenderer.VISIBLE_ICON)

        renderer.onBeforeClusterItemRenderedCaller(poi2, markerOptions)

        assertEquals(markerOptions.icon, PoiRenderer.REACHABLE_ICON)

        poiStatusMap.put(poi1, PoiMarkerViewModel.PointOfInterestStatus.VISITED)
        renderer.renderClusterItems(poiStatusMap)
        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)

        assertEquals(markerOptions.icon, PoiRenderer.VISITED_ICON)
    }
}

class PoiRendererTester(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<PoiItem>) : PoiRenderer(context, map, clusterManager) {
    fun onBeforeClusterItemRenderedCaller(item: PoiItem, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}