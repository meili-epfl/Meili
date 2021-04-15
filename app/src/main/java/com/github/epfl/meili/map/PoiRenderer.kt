package com.github.epfl.meili.map

import android.content.Context
import android.util.Log
import com.github.epfl.meili.poi.PoiService
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

open class PoiRenderer(context: Context?, map: GoogleMap?, val clusterManager: ClusterManager<PoiItem>) : DefaultClusterRenderer<PoiItem>(context, map, clusterManager) {

    private var poiStatusMap: Map<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>? = null

    override fun onBeforeClusterItemRendered(item: PoiItem, markerOptions: MarkerOptions) {
        val color : Float
        val icon: BitmapDescriptor
        if(poiStatusMap==null || !poiStatusMap!!.contains(item)){
            icon = DEFAULT_ICON
        }else{
            when(poiStatusMap!![item]){
                PoiMarkerViewModel.PointOfInterestStatus.REACHABLE -> icon = REACHABLE_ICON
                PoiMarkerViewModel.PointOfInterestStatus.VISITED -> icon = VISITED_ICON
                PoiMarkerViewModel.PointOfInterestStatus.VISIBLE -> icon = VISIBLE_ICON
                else -> icon = DEFAULT_ICON
            }
        }
        markerOptions.icon(icon)
    }

    fun renderClusterItems(poiStatusMap: Map<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>){
        clusterManager.clearItems()

        clusterManager.addItems(poiStatusMap.keys)

        clusterManager.cluster()

        this.poiStatusMap = poiStatusMap
    }

    companion object{
        val REACHABLE_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        val VISITED_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        val VISIBLE_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        val DEFAULT_ICON = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    }
}