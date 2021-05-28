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

    fun onUsersInfoReceived(
        users: Map<String, User>,
        map: Map<String, T>,
        postProcessing: (HashMap<String, Pair<T, User>>) -> List<Pair<String, Pair<T, User>>> = { it.toList() }
    ) {
        usersMap = usersMap + HashMap(users)
        val itemsAndUsersMap = HashMap<String, Pair<T, User>>()
        for ((uid, user) in users) {
            val value = map[uid]
            if (value != null) {
                itemsAndUsersMap[uid] = Pair(value, user)
            }
        }
        recyclerAdapter.submitList(postProcessing(itemsAndUsersMap))
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
        }
    }
}