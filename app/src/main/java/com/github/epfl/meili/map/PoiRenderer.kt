package com.github.epfl.meili.map

import android.content.Context
import android.util.Log
import com.github.epfl.meili.poi.PoiService
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class PoiRenderer(context: Context?, map: GoogleMap?, val clusterManager: ClusterManager<PoiItem>) : DefaultClusterRenderer<PoiItem>(context, map, clusterManager) {

    private var poiStatusMap: Map<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>? = null

    override fun onBeforeClusterItemRendered(item: PoiItem, markerOptions: MarkerOptions) {
        val color : Float
        if(poiStatusMap==null || !poiStatusMap!!.contains(item)){
            color = BitmapDescriptorFactory.HUE_RED
        }else{
            when(poiStatusMap!![item]){
                PoiMarkerViewModel.PointOfInterestStatus.REACHABLE -> color = BitmapDescriptorFactory.HUE_AZURE
                PoiMarkerViewModel.PointOfInterestStatus.VISITED -> color = BitmapDescriptorFactory.HUE_YELLOW
                PoiMarkerViewModel.PointOfInterestStatus.VISIBLE -> color = BitmapDescriptorFactory.HUE_GREEN
                else -> color = BitmapDescriptorFactory.HUE_RED

            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color))
    }

    fun renderClusterItems(poiStatusMap: Map<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>){
        //clusterManager.addItems(poiStatusMap.keys)

        clusterManager.clearItems()
        //clusterManager.cluster()

        clusterManager.addItems(poiStatusMap.keys)

        clusterManager.cluster()

        this.poiStatusMap = poiStatusMap
    }
}