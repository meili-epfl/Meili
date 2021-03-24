package com.github.epfl.meili.forum

import android.util.Log
import com.github.epfl.meili.forum.Post.Companion.toPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class MockPostService : PostService(){
    private var idCounter: Int = 0
    public var posts: ArrayList<Post> = ArrayList()
    private val TAG = "MockPostService"
    private val FAKE_ID = "FAKE_ID"
    private val FAKE_AUTHOR = "FAKE_AUTHOR"
    private val FAKE_TITLE = "FAKE_TITLE"
    private val FAKE_TEXT = "FAKE_TEXT"
    private var observers: ArrayList<Observer> = ArrayList()


    init {
            posts.add(Post(FAKE_ID, FAKE_AUTHOR, FAKE_TITLE, FAKE_TEXT))
            setChanged() // Tell Observable that its state has changed
            notifyObservers()
        }

    override suspend fun getPostFromId(id: String?): Post? {
        for(post:Post  in posts){
            if(post.id == id){
                return post;
            }
        }
        return null
    }

    override suspend fun getPosts(): List<Post> {
        return posts
    }

    override fun addPost(author: String, title: String, text: String) {
        posts.add(Post((idCounter++).toString(),author, title, text))
        notifyObservers()
    }

    override fun addObserver(o: Observer?) {
        super.addObserver(o)
        if(o != null && !observers.contains(o)){
            observers.add(o)
        }
        notifyObservers()
    }

    override fun notifyObservers() {
        super.notifyObservers()

        for(observer in observers){
            observer.update(this, posts)
        }
    }
}
