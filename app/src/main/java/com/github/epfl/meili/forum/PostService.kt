package com.github.epfl.meili.forum

import java.util.*

abstract class PostService : Observable() {

    private var observers: ArrayList<Observer> = ArrayList()
    public var posts: ArrayList<Post> = ArrayList()

    abstract fun getPostFromId(id: String?): Post?
    abstract fun getPosts(): List<Post>
    abstract fun addPost(author: String, title: String, text: String)

    override fun addObserver(o: Observer?) {
        super.addObserver(o)
        if (o != null && !observers.contains(o)) {
            observers.add(o)
        }
    }

    override fun notifyObservers() {
        super.notifyObservers()

        for (observer in observers) {
            observer.update(this, posts)
        }
    }
}