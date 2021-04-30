package com.github.epfl.meili.forum

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.PostViewModel

class ForumRecyclerAdapter(postViewModel: PostViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TAG = "ForumRecyclerAdapter"
    }

    private var items: List<Pair<String, Post>> = ArrayList()
    private var userId: String? = null
    private val postViewModel: PostViewModel = postViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false), postViewModel)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as PostViewHolder).bind(items[position], userId)

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, Post>>) {
        items = list
    }

    fun submitUserInfo(uid: String){
        userId = uid
    }

    class PostViewHolder(itemView: View, postViewModel: PostViewModel) : RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.post_author)
        private val title: TextView = itemView.findViewById(R.id.post_title)
        private val postId: TextView = itemView.findViewById(R.id.post_id)
        private val upvoteButton: ImageButton = itemView.findViewById(R.id.upvote_button)
        private val downvoteButton: ImageButton = itemView.findViewById(R.id.downovte_button)
        private val upvoteCount: TextView = itemView.findViewById(R.id.upvote_count)
        private val upvoteConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.upvote_ConstraintLayout)
        private val postViewModel = postViewModel

        fun bind(pair: Pair<String, Post>, userId: String?) {

            postId.text = pair.first

            val post = pair.second
            author.text = post.author
            title.text = post.title

            //show or hide up/downvote depending on user status
            val visibility = if(userId == null){
                View.GONE
            }else{
                View.VISIBLE
            }
            upvoteButton.visibility = visibility
            downvoteButton.visibility = visibility
            if(userId != null){
                setupButtons(post.upvoters, post.downvoters, userId, pair.first)
            }
            upvoteCount.text = (post.upvoters.size - post.downvoters.size).toString()

        }

        private fun setupButtons(upvoters: ArrayList<String>, downvoters: ArrayList<String>, userId: String, postId: String){
            if(upvoters.contains(userId)){
                upvoteButton.setImageResource(R.mipmap.upvote_filled_foreground)
                downvoteButton.setImageResource(R.mipmap.downvote_empty_foreground)
            }else if(downvoters.contains(userId)){
                upvoteButton.setImageResource(R.mipmap.upvote_empty_foreground)
                downvoteButton.setImageResource(R.mipmap.downvote_filled_foreground)
            }else{
                upvoteButton.setImageResource(R.mipmap.upvote_empty_foreground)
                downvoteButton.setImageResource(R.mipmap.downvote_empty_foreground)
            }
            upvoteButton.setOnClickListener {
                postViewModel.upvote(postId, userId)
            }
            downvoteButton.setOnClickListener{
                postViewModel.downvote(postId, userId)
            }
        }

    }
}