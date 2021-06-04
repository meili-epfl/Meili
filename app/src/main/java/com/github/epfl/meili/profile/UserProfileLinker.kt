package com.github.epfl.meili.profile

import android.content.Intent
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter

interface UserProfileLinker<T> : ClickListener {
    var recyclerAdapter: MeiliRecyclerAdapter<Pair<T, User>>
    var usersMap: Map<String, User>

    fun onUsersInfoReceived(users: Map<String, User>, map: Map<String, T>) {
        usersMap = usersMap + HashMap(users)
        val itemsAndUsersMap = HashMap<String, Pair<T, User>>()
        for ((uid, user) in users) {
            val item = map[uid]
            if (item != null) {
                itemsAndUsersMap[uid] = Pair(item, user)
            }
        }
        recyclerAdapter.submitList(itemsAndUsersMap.toList())
        recyclerAdapter.notifyDataSetChanged()
    }

    fun openUserProfile(friendUid: String) {
        val intent =
            Intent(MainApplication.applicationContext(), ProfileActivity::class.java).putExtra(
                ProfileActivity.USER_KEY,
                friendUid
            )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        MainApplication.applicationContext().startActivity(intent)
    }

    override fun onClicked(buttonId: Int, info: String) {
        when (buttonId) {
            R.id.userName -> openUserProfile(info)
            R.id.userImage -> openUserProfile(info)
        }
    }
}