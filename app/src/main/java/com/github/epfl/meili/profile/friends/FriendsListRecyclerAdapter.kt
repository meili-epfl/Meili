package com.github.epfl.meili.profile.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Friend

class FriendsListRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Pair<String, Friend>> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            FriendViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.friend, parent, false)
            )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as FriendViewHolder).bind(items[position])

    override fun getItemCount() = items.size

    fun submitList(list: List<Pair<String, Friend>>) {
        items = list
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.friend_name)

        // TODO: fetch and display Friend's info from Firestore: users/{friend.uid}
        fun bind(pair: Pair<String, Friend>) {
            val friend = pair.second
            name.text = friend.uid
        }
    }
}