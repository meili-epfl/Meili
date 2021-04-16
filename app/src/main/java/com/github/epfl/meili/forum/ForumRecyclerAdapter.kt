package com.github.epfl.meili.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post

class ForumRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Pair<String, Post>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as PostViewHolder).bind(items[position])

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, Post>>) {
        items = list
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.post_author)
        private val title: TextView = itemView.findViewById(R.id.post_title)
        private val postId: TextView = itemView.findViewById(R.id.post_id)

        fun bind(pair: Pair<String, Post>) {
            postId.text = pair.first

            val post = pair.second
            author.text = post.author
            title.text = post.title
        }
    }
}