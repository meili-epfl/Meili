package com.github.epfl.meili.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.VisitedPointOfInterest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PoiHistoryRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Pair<String, VisitedPointOfInterest>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PoiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.poi, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as PoiViewHolder).bind(items[position])

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, VisitedPointOfInterest>>) {
        items = list
    }

    class PoiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poiName: TextView = itemView.findViewById(R.id.poi_name)
        private val visitedDate: TextView = itemView.findViewById(R.id.visited_date)
        private val poiId: TextView = itemView.findViewById(R.id.poi_id)


        fun bind(pair: Pair<String, VisitedPointOfInterest>) {
            poiId.text = pair.first
            val poi = pair.second
            poiName.text = poi.poi?.name
            visitedDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(poi.dateVisited!!)
        }
    }
}