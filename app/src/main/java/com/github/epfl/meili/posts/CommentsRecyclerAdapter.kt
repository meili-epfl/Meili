package com.github.epfl.meili.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.util.MeiliRecyclerAdapter

class CommentsRecyclerAdapter :
    MeiliRecyclerAdapter<Comment>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as CommentViewHolder).bind(items[position])

    class CommentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.comment_author)
        private val text: TextView = itemView.findViewById(R.id.comment_text)

        fun bind(pair: Pair<String, Comment>) {
            val comment = pair.second
            author.text = comment.author
            text.text = comment.text
        }
    }
}