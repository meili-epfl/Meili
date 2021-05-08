package com.github.epfl.meili.profile.friends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.squareup.picasso.Picasso

class FriendsListRecyclerAdapter(val clickListener: ClickListener) : MeiliRecyclerAdapter<User>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.friend, parent, false),
            clickListener
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as FriendViewHolder).bind(items[position])

    class FriendViewHolder(itemView: View, private val listener: ClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val name: TextView = itemView.findViewById(R.id.friendName)
        private val picture: ImageView = itemView.findViewById(R.id.friendImage)
        private lateinit var user: User

        init {
            name.setOnClickListener(this)
            itemView.findViewById<Button>(R.id.friend_chat_button).setOnClickListener(this)
        }

        fun bind(pair: Pair<String, User>) {
            Log.d("Recycler friend", pair.second.username)
            val friend = pair.second
            user = friend
            name.text = friend.username
            Picasso.get().load("images/avatars/${friend.uid}").into(picture)
        }

        override fun onClick(v: View) {
            Log.d("Friend view holder", user.uid)
            listener.onClicked(v.id, user.uid)
        }
    }
}