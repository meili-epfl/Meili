package com.github.epfl.meili.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review

class ReviewRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TAG: String = "ReviewRecyclerAdapter"
    }

    private var items: List<Pair<String, Review>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ReviewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as ReviewViewHolder).bind(items[position])

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, Review>>) {
        items = list
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ratingBar: RatingBar = itemView.findViewById(R.id.review_rating)
        private val title: TextView = itemView.findViewById(R.id.review_title)
        private val summary: TextView = itemView.findViewById(R.id.review_summary)
        private val author: TextView = itemView.findViewById(R.id.review_author)

        fun bind(pair: Pair<String, Review>) {
            val review = pair.second
            ratingBar.rating = review.rating
            title.text = review.title
            summary.text = review.summary
            author.text = pair.first
        }
    }
}