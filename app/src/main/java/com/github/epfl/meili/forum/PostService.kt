package com.github.epfl.meili.forum

import com.github.epfl.meili.models.ChatMessage
import java.util.*

abstract class PostService : Observable() {

    abstract suspend fun getPostFromId(id: String?): Post?
    abstract suspend fun getPosts(): List<Post>
    abstract fun addPost(author: String, title: String, text: String)
}