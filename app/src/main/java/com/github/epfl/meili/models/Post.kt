package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
        var postId: String = "",
        var poiKey: String = "",
        var authorUid: String = "",
        var title: String = "",
        val timestamp: Long = -1,
        var text: String = "",
        var upvoters: ArrayList<String> = arrayListOf(),
        var downvoters: ArrayList<String> = arrayListOf()

) : Parcelable {
    companion object {
        const val TAG = "Post"
        const val POI_KEY_FIELD = "poiKey"
        const val AUTHOR_UID_FIELD = "authorUid"
    }
}