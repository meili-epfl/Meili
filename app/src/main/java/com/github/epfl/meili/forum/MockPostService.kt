package com.github.epfl.meili.forum

class MockPostService : PostService() {
    private var idCounter: Int = 0
    private val TAG = "MockPostService"
    private val FAKE_ID = "FAKE_ID"
    private val FAKE_AUTHOR = "FAKE_AUTHOR"
    private val FAKE_TITLE = "FAKE_TITLE"
    private val FAKE_TEXT = "FAKE_TEXT"


    init {
        posts.add(Post(FAKE_ID, FAKE_AUTHOR, FAKE_TITLE, FAKE_TEXT))
        setChanged() // Tell Observable that its state has changed
        notifyObservers()
    }

    override fun getPostFromId(id: String?): Post? {
        for (post: Post in posts) {
            if (post.id == id) {
                return post;
            }
        }
        return null
    }

    override fun getPosts(): List<Post> {
        return posts
    }

    override fun addPost(author: String, title: String, text: String) {
        posts.add(Post((idCounter++).toString(), author, title, text))
        notifyObservers()
    }
}
