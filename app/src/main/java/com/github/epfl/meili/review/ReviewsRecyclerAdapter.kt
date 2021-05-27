package com.github.epfl.meili.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.MeiliWithUserRecyclerViewHolder

class ReviewsRecyclerAdapter(private val clickListener: ClickListener) : MeiliRecyclerAdapter<Pair<Review, User>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ReviewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false), clickListener)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as ReviewViewHolder).bind(items[position].second.second, items[position].second.first)

    class ReviewViewHolder(itemView: View, listener: ClickListener) : MeiliWithUserRecyclerViewHolder<Review>(itemView, listener), View.OnClickListener {
        private val ratingBar: RatingBar = itemView.findViewById(R.id.review_rating)
        private val title: TextView = itemView.findViewById(R.id.review_title)
        private val summary: TextView = itemView.findViewById(R.id.review_summary)


        override fun bind(user: User, other: Review) {
            super.bind(user, other)

            ratingBar.rating = other.rating
            title.text = other.title
            summary.text = other.summary
        }
    }
}