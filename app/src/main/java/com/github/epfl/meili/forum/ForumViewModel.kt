package com.github.epfl.meili.forum

import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.MeiliViewModel

class ForumViewModel: MeiliViewModel<Post>() {
    fun upvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key,uid, true)

    fun downvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key,uid, false)
}