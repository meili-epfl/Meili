package com.github.epfl.meili.profile.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.MeiliWithUserRecyclerViewHolder

class FriendsListRecyclerAdapter(private val clickListener: ClickListener) : MeiliRecyclerAdapter<Pair<Friend, User>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            FriendViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.friend, parent, false),
                    clickListener
            )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as FriendViewHolder).bind(items[position].second.second, "")

    class FriendViewHolder(itemView: View, listener: ClickListener) :
            MeiliWithUserRecyclerViewHolder<String>(itemView, listener), View.OnClickListener {

        init {
            itemView.findViewById<Button>(R.id.friend_chat_button).setOnClickListener(this)
            itemView.findViewById<TextView>(R.id.userName).setOnClickListener(this)
        }
    }
}