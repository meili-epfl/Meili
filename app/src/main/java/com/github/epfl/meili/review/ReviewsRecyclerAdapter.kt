package com.github.epfl.meili.review

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ReviewsRecyclerAdapter(private val clickListener: ClickListener) : MeiliRecyclerAdapter<Pair<Review, User>>(){
    companion object {
        var imageAvatarPath: (String) -> String = { uid -> "images/avatars/${uid}" }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ReviewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false), clickListener)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as ReviewViewHolder).bind(items[position])

    class ReviewViewHolder(itemView: View, private val listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val ratingBar: RatingBar = itemView.findViewById(R.id.review_rating)
        private val title: TextView = itemView.findViewById(R.id.review_title)
        private val summary: TextView = itemView.findViewById(R.id.review_summary)
        private val author: TextView = itemView.findViewById(R.id.review_author_name)
        private val authorImage: CircleImageView = itemView.findViewById(R.id.review_author_image)
        private lateinit var user: User

        init {
            itemView.findViewById<TextView>(R.id.review_author_name).setOnClickListener(this)
        }

        fun bind(pair: Pair<String, Pair<Review, User>>) {
            val review = pair.second.first
            user = pair.second.second
            ratingBar.rating = review.rating
            title.text = review.title
            summary.text = review.summary
            author.text = user.username
            FirebaseStorageService.getDownloadUrl(
                    imageAvatarPath(user.uid),
                    { uri -> Picasso.get().load(uri).into(authorImage) },
                    { /* do nothing in case of failure */ }
            )
        }

        override fun onClick(v: View) {
            Log.d("Friend view holder", user.uid)
            listener.onClicked(v.id, user.uid)
        }
    }


}