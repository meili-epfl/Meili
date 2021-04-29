package com.github.epfl.meili.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.models.Post
import java.util.*

class PostViewModel: ViewModel(), Observer {

    private val mElements: MutableLiveData<Map<String, Post>> = MutableLiveData()

    private lateinit var database: AtomicPostFirestoreDatabase

    fun setDatabase(database: AtomicPostFirestoreDatabase) {
        this.database = database
        database.addObserver(this)
    }

    fun upvote(key: String, uid: String) = database.upvote(key,uid)

    fun downvote(key: String, uid: String) = database.downvote(key,uid)

    fun getElements(): LiveData<Map<String, Post>> = mElements

    fun addElement(id: String, element: Post) = database.addElement(id, element)

    override fun update(o: Observable?, arg: Any?) {
        val reviews: Map<String, Post> = database.elements
        mElements.postValue(reviews)
    }

    fun onDestroy() {
        database.onDestroy()
    }
}