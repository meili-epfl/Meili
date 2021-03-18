package com.github.epfl.meili.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review

class ReviewRecyclerAdapter(private var items: List<Review>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReviewViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount() = items.size

    class ReviewViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val rating: TextView = itemView.findViewById(R.id.review_rating)
        val title: TextView = itemView.findViewById(R.id.review_title)
        val summary: TextView = itemView.findViewById(R.id.review_summary)
        val author: TextView = itemView.findViewById(R.id.review_author)

        fun bind(review: Review) {
            rating.text = review.rating.toString()
            title.text = review.title
            summary.text = review.summary
            author.text = review.uid
        }
    }
}