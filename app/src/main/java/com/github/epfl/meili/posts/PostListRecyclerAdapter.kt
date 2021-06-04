package com.github.epfl.meili.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.ImageSetter
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.MeiliWithUserRecyclerViewHolder


class PostListRecyclerAdapter(private val viewModel: PostListViewModel, private val listener: ClickListener,
                              private val showPOI: Boolean) :
        MeiliRecyclerAdapter<Pair<Post, User>>() {

    private var userId: String? = null

    companion object {
        const val TAG = "PostListRecyclerAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false),
            listener
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as PostViewHolder).bind(
            items[position].second.second,
            items[position].second.first,
            userId
        )

    /**
     * Update recycler view with user's info
     */
    fun submitUserInfo(uid: String) {
        userId = uid
    }

    inner class PostViewHolder(itemView: View, listener: ClickListener) :
        MeiliWithUserRecyclerViewHolder<Post>(itemView, listener) {
        private val title: TextView = itemView.findViewById(R.id.post_title)
        private val postId: TextView = itemView.findViewById(R.id.post_id)
        private val poiName: TextView = itemView.findViewById(R.id.post_poi_name)
        private val upvoteButton: ImageButton = itemView.findViewById(R.id.upvote_button)
        private val downvoteButton: ImageButton = itemView.findViewById(R.id.downovte_button)
        private val upvoteCount: TextView = itemView.findViewById(R.id.upvote_count)
        private val image: ImageView = itemView.findViewById(R.id.forum_post_image)

        /*
         * Binds the post contained in `pair` to its viewholder
         */
        fun bind(user: User, post: Post, userId: String?) {
            super.bind(user, post)

            postId.text = post.postId()
            poiName.text = post.poiName
            title.text = post.title

            poiName.isVisible = showPOI

            //show or hide up/downvote depending on user status
            upvoteButton.isVisible = userId != null
            downvoteButton.isVisible = userId != null
            if (userId != null) {
                setupButtons(post.upvoters, post.downvoters, userId, post.postId())
            }
            upvoteCount.text = (post.upvoters.size - post.downvoters.size).toString()

            if (post.hasPhoto) {
                ImageSetter.setImageInto(post.postId(), image, ImageSetter.imagePostPath)
            } else {
                image.setImageResource(0) // Clear image, otherwise it takes image from previous post
            }
        }

        private fun setupButtons(
            upvoters: ArrayList<String>,
            downvoters: ArrayList<String>,
            userId: String,
            postId: String
        ) {
            updateVoteButtons(upvoters.contains(userId), downvoters.contains(userId))
            upvoteButton.setOnClickListener { viewModel.upvote(postId, userId) }
            downvoteButton.setOnClickListener { viewModel.downvote(postId, userId) }
        }

        private fun updateVoteButtons(up: Boolean, down: Boolean) {
            val upRes = if (up) {
                R.mipmap.upvote_filled
            } else {
                R.mipmap.upvote_empty
            }

            val downRes = if (down) {
                R.mipmap.downvote_filled
            } else {
                R.mipmap.downvote_empty
            }

            upvoteButton.setImageResource(upRes)
            downvoteButton.setImageResource(downRes)
        }
    }
}
