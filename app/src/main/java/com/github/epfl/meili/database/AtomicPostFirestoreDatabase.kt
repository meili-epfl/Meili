package com.github.epfl.meili.database

import com.github.epfl.meili.models.Post

class AtomicPostFirestoreDatabase(path: String) : FirestoreDatabase<Post>(path, Post::class.java) {
    companion object {
        private const val upVoters = "upvoters"
        private const val downVoters = "downvoters"
    }

    /**
     * Makes the user corresponding to `userUid` upvote or downvote depending on `isUpvote` the post corresponding to `key`, in an atomic way
     */
    @Suppress("UNCHECKED_CAST")
    fun upDownVote(key: String, userUid: String, isUpvote: Boolean) {
        val sfDocRef = ref.document(key)
        databaseProvider().runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            val fieldName: String = if(isUpvote) upVoters else downVoters
            val otherFieldName: String = if(isUpvote) downVoters else upVoters
            val voters = ArrayList(snapshot.get(fieldName)!! as List<String>)
            val otherVoters = ArrayList(snapshot.get(otherFieldName)!! as List<String>)
            if (voters.contains(userUid)) {
                voters.remove(userUid)
            } else {
                if (otherVoters.contains(userUid)) {
                    otherVoters.remove(userUid)
                }
                voters.add(userUid)
            }

            //update to new up/downvoters list
            transaction.update(sfDocRef, fieldName, voters)
            transaction.update(sfDocRef, otherFieldName, otherVoters)
        }
    }

}