package com.github.epfl.meili.util

import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.models.Review

abstract class MeiliRecyclerAdapter<T>: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Pair<String, T>> = ArrayList()

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, T>>) {
        items = list
    }
}