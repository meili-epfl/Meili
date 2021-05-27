package com.github.epfl.meili.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter

import com.github.epfl.meili.util.MeiliWithUserRecyclerViewHolder

class CommentsRecyclerAdapter(private val listener: ClickListener) :
    MeiliRecyclerAdapter<Pair<Comment, User>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment, parent, false),
            listener
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as CommentViewHolder).bind(
            items[position].second.second,
            items[position].second.first
        )

    class CommentViewHolder(itemView: View, listener: ClickListener) :
        MeiliWithUserRecyclerViewHolder<Comment>(itemView, listener), View.OnClickListener {

        private val text: TextView = itemView.findViewById(R.id.comment_text)

        override fun bind(user: User, other: Comment) {
            super.bind(user, other)

            text.text = other.text
        }

    }
}