package com.github.epfl.meili.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

object PostViewModel : ViewModelWithPostService(), Observer {

    private val _post = MutableLiveData<Post?>() // makes it easier to manage
    val post: LiveData<Post?> = _post // public post to expose safely to the View (not mutable)
    var post_id: String = ""

    init {
        postService.addObserver(this)
    }

    /** Called when notified by observable */
    override fun update(o: Observable?, arg: Any?) {
        syncPost()
    }

    /** Synchronize post */
    private fun syncPost() {
        viewModelScope.launch { // Asynchronous block for viewModels
            _post.value = postService.getPostFromId(post_id)
        }
    }

    /** Change id of post to be seen */
    fun setID(id: String) {
        post_id = id
        syncPost()
    }
}