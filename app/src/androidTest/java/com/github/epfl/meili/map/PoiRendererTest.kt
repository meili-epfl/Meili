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

@RunWith(AndroidJUnit4::class)
class PoiRendererTest {
    private lateinit var renderer: PoiRendererTester

    private val poi1 = PoiItem(PointOfInterest(41.075000, 1.130870, "place1", "place1"))
    private val poi2 = PoiItem(PointOfInterest(41.063563, 1.083658, "place2", "place2"))

    @Test
    fun generalTest() {
        MapsInitializer.initialize(MainApplication.applicationContext())
        Looper.prepare()

        val mockClusterManager = Mockito.mock(ClusterManager::class.java)

        renderer = PoiRendererTester(MainApplication.applicationContext(), null, mockClusterManager as ClusterManager<PoiItem>)

        val poiStatusMap = HashMap<PoiItem, PointOfInterestStatus>()

        poiStatusMap[poi1] = PointOfInterestStatus.VISIBLE
        poiStatusMap[poi2] = PointOfInterestStatus.REACHABLE

        // Test render cluster items
        renderer.renderClusterItems(poiStatusMap)

        val markerOptions = MarkerOptions()

        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)
        assertEquals(markerOptions.icon, PoiRenderer.VISIBLE_ICON)

        renderer.onBeforeClusterItemRenderedCaller(poi2, markerOptions)

        assertEquals(markerOptions.icon, PoiRenderer.REACHABLE_ICON)

        poiStatusMap[poi1] = PointOfInterestStatus.VISITED
        renderer.renderClusterItems(poiStatusMap)
        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)

        assertEquals(markerOptions.icon, PoiRenderer.VISITED_ICON)

        // Test Meili Lens Poi
        renderer.renderMeiliLensPoi(poi1)

        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)

        assertEquals(markerOptions.icon, PoiRenderer.MEILI_LENS_ICON)

        renderer.renderMeiliLensPoi(null)

        renderer.onBeforeClusterItemRenderedCaller(poi1, markerOptions)

        assertEquals(markerOptions.icon, PoiRenderer.VISITED_ICON)
    }
}

class PoiRendererTester(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<PoiItem>) : PoiRenderer(context, map, clusterManager) {
    fun onBeforeClusterItemRenderedCaller(item: PoiItem, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}