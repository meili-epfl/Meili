package com.github.epfl.meili.forum

interface PostService {

    suspend fun getPostFromId(id: String?): Post?

    suspend fun getPosts(): List<Post>

    fun addPost(author: String, title: String, text: String)
}