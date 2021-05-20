package com.github.epfl.meili.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.MeiliRecyclerAdapter

class PostListRecyclerAdapter(private val viewModel: PostListViewModel) :
        MeiliRecyclerAdapter<Post>() {
    private var userId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            PostViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false),
            )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as PostViewHolder).bind(items[position], userId)

    fun submitUserInfo(uid: String) {
        userId = uid
    }

    inner class PostViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.post_author)
        private val title: TextView = itemView.findViewById(R.id.post_title)
        private val postId: TextView = itemView.findViewById(R.id.post_id)
        private val upvoteButton: ImageButton = itemView.findViewById(R.id.upvote_button)
        private val downvoteButton: ImageButton = itemView.findViewById(R.id.downovte_button)
        private val upvoteCount: TextView = itemView.findViewById(R.id.upvote_count)

        fun bind(pair: Pair<String, Post>, userId: String?) {
            postId.text = pair.first

            val post = pair.second
            author.text = post.authorUid
            title.text = post.title

            //show or hide up/downvote depending on user status
            val visibility = if (userId == null) {
                View.GONE
            } else {
                View.VISIBLE
            }
            upvoteButton.visibility = visibility
            downvoteButton.visibility = visibility
            if (userId != null) {
                setupButtons(post.upvoters, post.downvoters, userId, pair.first)
            }
            upvoteCount.text = (post.upvoters.size - post.downvoters.size).toString()
        }

        private fun setupButtons(
                upvoters: ArrayList<String>,
                downvoters: ArrayList<String>,
                userId: String,
                postId: String
        ) {
            when {
                upvoters.contains(userId) -> {
                    upvoteButton.setImageResource(R.mipmap.upvote_filled_foreground)
                    downvoteButton.setImageResource(R.mipmap.downvote_empty_foreground)
                }
                downvoters.contains(userId) -> {
                    upvoteButton.setImageResource(R.mipmap.upvote_empty_foreground)
                    downvoteButton.setImageResource(R.mipmap.downvote_filled_foreground)
                }
                else -> {
                    upvoteButton.setImageResource(R.mipmap.upvote_empty_foreground)
                    downvoteButton.setImageResource(R.mipmap.downvote_empty_foreground)
                }
            }
            upvoteButton.setOnClickListener { viewModel.upvote(postId, userId) }
            downvoteButton.setOnClickListener { viewModel.downvote(postId, userId) }
        }
    }
}