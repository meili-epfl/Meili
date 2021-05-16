package com.github.epfl.meili.util

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import de.hdodenhof.circleimageview.CircleImageView

abstract class MeiliWithUserRecyclerViewHolder<T>(itemView: View, private val listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val author: TextView = itemView.findViewById(R.id.userName)
    private val authorImage: CircleImageView = itemView.findViewById(R.id.userImage)
    private lateinit var user: User

    open fun bind(user: User, other: T) {
        this.user = user
        author.text = user.username

        ImageSetter.setImageInto(user.uid, authorImage)
    }

    override fun onClick(v: View) {
        Log.d("Meili with user recycle", v.id.toString())
        listener.onClicked(v.id, user.uid)
    }
}