package com.github.epfl.meili.profile.friends

import androidx.lifecycle.MutableLiveData
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.models.Friend
import java.util.*
import kotlin.collections.ArrayList


class FriendsListViewModel(currentUserId: String) : Observer {
    private var path: String = "friends/$currentUserId/friends"
    private var database: Database<Friend> = FirestoreDatabase(path, Friend::class.java)

    val mFriendsList: MutableLiveData<List<Friend>> = MutableLiveData(ArrayList())

    init {
        database.addObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        mFriendsList.value = database.elements.values.toList()
    }
}