package com.github.epfl.meili.profile

import android.content.Intent
import android.util.Log
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter

interface UserProfileLinker<T> : ClickListener {
    var recyclerAdapter: MeiliRecyclerAdapter<Pair<T, User>>
    var usersMap: Map<String, User>

    fun onUsersInfoReceived(users: Map<String, User>, map: Map<String, T>) {
        Log.d("UserProfileLinker", users.toString())
        Log.d("UserProfileLinker", map.toString())
        usersMap = usersMap + HashMap(users)
        val reviewsAndUsersMap = HashMap<String, Pair<T, User>>()
        for ((uid, user) in users) {
            val value = map[uid]
            if (value != null) {
                reviewsAndUsersMap[uid] = Pair(value, user)
            }
        }
        recyclerAdapter.submitList(reviewsAndUsersMap.toList())
        recyclerAdapter.notifyDataSetChanged()
    }

    fun openUserProfile(friendUid: String) {
        val intent =
            Intent(MainApplication.applicationContext(), ProfileActivity::class.java).putExtra(
                ProfileActivity.USER_KEY,
                friendUid
            )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        MainApplication.applicationContext().startActivity(intent)
    }

    override fun onClicked(buttonId: Int, info: String) {
        when (buttonId) {
            R.id.userName -> openUserProfile(info)
        }
    }
}