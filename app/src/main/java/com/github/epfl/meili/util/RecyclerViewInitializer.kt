package com.github.epfl.meili.util

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewInitializer {
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