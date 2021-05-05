package com.github.epfl.meili.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.PointOfInterest

class PoiHistoryRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Pair<String, PointOfInterest>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PoiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.poi, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as PoiViewHolder).bind(items[position])

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, PointOfInterest>>) {
        items = list
    }

    class PoiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poiName: TextView = itemView.findViewById(R.id.poi_name)

        fun bind(pair: Pair<String, PointOfInterest>) {
            val poi = pair.second
            poiName.text = poi.name
        }
    }
}