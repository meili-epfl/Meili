package com.github.epfl.meili.forum

import java.util.*

/** Abstract class for adding and retrieving posts from and to a database*/
abstract class PostService : Observable() {

    abstract suspend fun getPostFromId(id: String?): Post?

    abstract suspend fun getPosts(): List<Post>

    abstract fun addPost(author: String, title: String, text: String)
}