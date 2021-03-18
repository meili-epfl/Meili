package com.github.epfl.meili.forum

/** Interface for adding and retrieving posts from and to the database*/
interface PostService {

    suspend fun getPostFromId(id: String?): Post?

    suspend fun getPosts(): List<Post>

    fun addPost(author: String, title: String, text: String)
}