package com.github.epfl.meili.util

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Utility to initialize a recycler view
 */
object RecyclerViewInitializer {
    /**
     * Initializes the given recycler view using the given adapter and activity
     */
    fun initRecyclerView(
        recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        recyclerView: RecyclerView,
        activity: AppCompatActivity
    ) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(TopSpacingItemDecoration())
            adapter = recyclerAdapter
        }
    }
}