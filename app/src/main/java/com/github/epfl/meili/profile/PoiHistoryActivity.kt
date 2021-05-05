package com.github.epfl.meili.profile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.VisitedPointOfInterest
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration


class PoiHistoryActivity : AppCompatActivity() {
    companion object {
        private const val CARD_PADDING: Int = 30
    }

    private lateinit var recyclerAdapter: PoiHistoryRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<VisitedPointOfInterest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi_history)

        title = "POI History"

        val userKey = Auth.getCurrentUser()!!.uid
        initRecyclerView()
        initViewModel(userKey)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun initViewModel(userKey: String) {
        @Suppress("UNCHECKED_CAST")
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<VisitedPointOfInterest>

        viewModel.setDatabase(
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
        recyclerAdapter.submitList(map.toList())
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        recyclerAdapter = PoiHistoryRecyclerAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.poi_history_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PoiHistoryActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = recyclerAdapter
        }
    }
}