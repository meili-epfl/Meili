package com.github.epfl.meili.util

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

abstract class MeiliRecyclerAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Pair<String, T>> = ArrayList()

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, T>>) {
        Log.d("SubmitList", list.toString())
        items = list
    }

    abstract fun notifyDataSetChanged()
}

