package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var author: String = "",
    var title: String = "",
    val timestamp: Long = -1,
    var text: String = ""
) : Parcelable {
    companion object {
        const val TAG = "Post"
    }
}