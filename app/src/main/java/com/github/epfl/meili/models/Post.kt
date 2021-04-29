package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var author: String = "",
    var title: String = "",
    var text: String = "",
    var upvoters: ArrayList<String> = arrayListOf(),
    var downvoters: ArrayList<String> = arrayListOf()
) : Parcelable {
    companion object {
        const val TAG = "Post"
    }
}