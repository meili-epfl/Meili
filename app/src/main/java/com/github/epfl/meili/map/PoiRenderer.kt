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
open class PoiRenderer(context: Context?, map: GoogleMap?, private val clusterManager: ClusterManager<PoiItem>)
    : DefaultClusterRenderer<PoiItem>(context, map, clusterManager) {

    private var poiStatusMap: Map<PoiItem, PointOfInterestStatus>? = null
    private var meiliLensPoi: PoiItem? = null

    override fun onBeforeClusterItemRendered(item: PoiItem, markerOptions: MarkerOptions) {
        val icon: BitmapDescriptor = if (meiliLensPoi == item) {
            MEILI_LENS_ICON
        } else if (poiStatusMap == null || !poiStatusMap!!.contains(item)) {
            DEFAULT_ICON
        } else {
            when (poiStatusMap!![item]) {
                PointOfInterestStatus.REACHABLE -> REACHABLE_ICON
                PointOfInterestStatus.VISITED -> VISITED_ICON
                PointOfInterestStatus.VISIBLE -> VISIBLE_ICON
                else -> DEFAULT_ICON
            }
        }
        markerOptions.icon(icon)
    }

    fun renderClusterItems(poiStatusMap: Map<PoiItem, PointOfInterestStatus>) {
        clusterManager.clearItems()

        clusterManager.addItems(poiStatusMap.keys)

        clusterManager.cluster()

        this.poiStatusMap = poiStatusMap
    }

    companion object {
        val REACHABLE_ICON: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        val VISITED_ICON: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        val VISIBLE_ICON: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        val DEFAULT_ICON: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        val MEILI_LENS_ICON: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
    }
}