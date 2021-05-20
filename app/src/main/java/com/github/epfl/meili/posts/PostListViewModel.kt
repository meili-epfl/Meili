package com.github.epfl.meili.posts

import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.MeiliViewModel

open class PostListViewModel: MeiliViewModel<Post>() {
    fun upvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key, uid, true)

    fun downvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key, uid, false)
}