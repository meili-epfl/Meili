package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val text: String = "",
    var fromId: String = "",
    val toId: String = "",
    val timestamp: Long = -1,
    val fromName: String = ""
) : Parcelable
