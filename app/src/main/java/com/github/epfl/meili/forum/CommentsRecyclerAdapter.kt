package com.github.epfl.meili.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.MeiliViewModel

class CommentsRecyclerAdapter(private val viewModel: MeiliViewModel<Comment>) :
    MeiliRecyclerAdapter<Comment>() {
    private var userId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment, parent, false),
            viewModel
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as CommentViewHolder).bind(items[position], userId)

    class CommentViewHolder(itemView: View, private val viewModel: MeiliViewModel<Comment>) :
        RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.comment_author)
        private val text: TextView = itemView.findViewById(R.id.comment_text)
        private val commentId: TextView = itemView.findViewById(R.id.comment_id)

        fun bind(pair: Pair<String, Comment>, userId: String?) {
            commentId.text = pair.first

            val comment = pair.second
            author.text = comment.author
            text.text = comment.text
        }
    }
}