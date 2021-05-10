package com.github.epfl.meili.forum

import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.MeiliViewModel

class ForumViewModel: MeiliViewModel<Post>() {
    /**
     * Makes the user corresponding to `uid` upvote the post corresponding to `key`, in an atomic way
     */
    fun upvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key,uid, true)

    /**
     * Makes the user corresponding to `uid` downvote the post corresponding to `key`, in an atomic way
     */
    fun downvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key,uid, false)
}