package com.github.epfl.meili.profile.poihistory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.VisitedPointOfInterest
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration
import java.util.*


class PoiHistoryActivity : AppCompatActivity() {
    companion object {
        private const val CARD_PADDING: Int = 30
        private const val ACTIVITY_TITLE = "POI History"

        fun addPoiToHistory(userKey: String, poi: PointOfInterest) {
            FirestoreDatabase( // add to poi history
                "poi-history/$userKey/poi-history",
                VisitedPointOfInterest::class.java
            ).addElement(poi.uid, VisitedPointOfInterest(poi))
        }
    }

    private lateinit var radapter: PoiHistoryRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<VisitedPointOfInterest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi_history)

        title = ACTIVITY_TITLE

        val userKey = Auth.getCurrentUser()!!.uid
        initRecyclerView()
        initViewModel(userKey)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun onPoiHistoryButtonClick(view: View) {
        startActivity(
            Intent(this, ForumActivity::class.java).putExtra(
                MapActivity.POI_KEY,
                viewModel.getElements().value?.get((view.findViewById(R.id.poi_id) as TextView).text.toString())?.poi
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewModel(userKey: String) {
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<VisitedPointOfInterest>

        viewModel.initDatabase(
            FirestoreDatabase(
                "poi-history/$userKey/poi-history",
                VisitedPointOfInterest::class.java
            )
        )
        viewModel.getElements().observe(this, { map ->
            poiHistoryMapListener(map)
        })
    }

    private fun poiHistoryMapListener(map: Map<String, VisitedPointOfInterest>) {
        val list = map.toList()
            .sortedWith { o1, o2 -> o2.second.dateVisited!!.compareTo(o1.second.dateVisited) }
        radapter.submitList(list)
        radapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        val recycler: RecyclerView = findViewById(R.id.poi_history_recycler_view)
        radapter = PoiHistoryRecyclerAdapter()
        recycler.apply {
            layoutManager = LinearLayoutManager(this@PoiHistoryActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = radapter
        }
    }
}