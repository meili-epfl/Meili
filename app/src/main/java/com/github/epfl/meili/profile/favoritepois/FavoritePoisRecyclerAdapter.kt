package com.github.epfl.meili.profile.favoritepois

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.MeiliRecyclerAdapter

class FavoritePoisRecyclerAdapter : MeiliRecyclerAdapter<PointOfInterest>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PoiViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.poi, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pvh = (holder as PoiViewHolder)
        pvh.bind(items[position])
    }


    class PoiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poiName: TextView = itemView.findViewById(R.id.poi_name)
        private val poiId: TextView = itemView.findViewById(R.id.poi_id)


        fun bind(pair: Pair<String, PointOfInterest>) {
            poiId.text = pair.first
            val poi = pair.second
            poiName.text = poi.name
        }
    }
}