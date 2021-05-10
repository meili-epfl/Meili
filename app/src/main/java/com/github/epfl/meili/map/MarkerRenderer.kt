package com.github.epfl.meili.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * PoiRenderer is the class which renders POIs as Markers on the Map and also handles cluster rendering
 */
open class MarkerRenderer(context: Context?, map: GoogleMap?, private val clusterManager: ClusterManager<MarkerItem>)
    : DefaultClusterRenderer<MarkerItem>(context, map, clusterManager) {

    private var poiStatusMap: Map<MarkerItem, MarkerViewModel.PointOfInterestStatus>? = null

    override fun onBeforeClusterItemRendered(item: MarkerItem, markerOptions: MarkerOptions) {
        val icon: BitmapDescriptor
        icon = if (poiStatusMap == null || !poiStatusMap!!.contains(item)) {
            DEFAULT_ICON
        } else {
            when (poiStatusMap!![item]) {
                MarkerViewModel.PointOfInterestStatus.REACHABLE -> REACHABLE_ICON
                MarkerViewModel.PointOfInterestStatus.VISITED -> VISITED_ICON
                MarkerViewModel.PointOfInterestStatus.VISIBLE -> VISIBLE_ICON
                else -> DEFAULT_ICON
            }
        }
        markerOptions.icon(icon)
    }

    /**
     * Renders all cluster items in `poiStatusMap`
     */
    fun renderClusterItems(poiStatusMap: Map<MarkerItem, MarkerViewModel.PointOfInterestStatus>) {
        clusterManager.clearItems()

        clusterManager.addItems(poiStatusMap.keys)

        clusterManager.cluster()

        this.poiStatusMap = poiStatusMap
    }

    companion object {
        val REACHABLE_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        val VISITED_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        val VISIBLE_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        val DEFAULT_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    }
}