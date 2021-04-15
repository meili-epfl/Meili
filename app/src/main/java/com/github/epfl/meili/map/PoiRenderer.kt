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
open class PoiRenderer(context: Context?, map: GoogleMap?, val clusterManager: ClusterManager<PoiItem>)
    : DefaultClusterRenderer<PoiItem>(context, map, clusterManager) {

    private var poiStatusMap: Map<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>? = null

    override fun onBeforeClusterItemRendered(item: PoiItem, markerOptions: MarkerOptions) {
        val icon: BitmapDescriptor
        if (poiStatusMap == null || !poiStatusMap!!.contains(item)) {
            icon = DEFAULT_ICON
        } else {
            icon = when (poiStatusMap!![item]) {
                PoiMarkerViewModel.PointOfInterestStatus.REACHABLE -> REACHABLE_ICON
                PoiMarkerViewModel.PointOfInterestStatus.VISITED -> VISITED_ICON
                PoiMarkerViewModel.PointOfInterestStatus.VISIBLE -> VISIBLE_ICON
                else -> DEFAULT_ICON
            }
        }
        markerOptions.icon(icon)
    }

    fun renderClusterItems(poiStatusMap: Map<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>) {
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