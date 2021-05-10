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
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendsListRecyclerAdapter(private val clickListener: ClickListener) : MeiliRecyclerAdapter<User>() {
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
        private val picture: CircleImageView = itemView.findViewById(R.id.friendImage)
        private lateinit var user: User

        init {
            name.setOnClickListener(this)
            itemView.findViewById<Button>(R.id.friend_chat_button).setOnClickListener(this)
        }

        fun bind(pair: Pair<String, User>) {
            Log.d("Recycler friend", pair.second.username)
            user = pair.second
            name.text = user.username
            FirebaseStorageService.getDownloadUrl(
                    "images/avatars/${user.uid}",
                    { uri -> Picasso.get().load(uri).into(picture) },
                    { /* do nothing in case of failure */ }
            )
        }

        override fun onClick(v: View) {
            Log.d("Friend view holder", user.uid)
            listener.onClicked(v.id, user.uid)
        }
    }
}