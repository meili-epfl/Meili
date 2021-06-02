

package com.github.epfl.meili.posts

import android.content.Intent
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.ListSorter
import com.github.epfl.meili.util.ListSorter.Companion.NEWEST
import com.github.epfl.meili.util.ListSorter.Companion.OLDEST
import com.github.epfl.meili.util.ListSorter.Companion.POPULAR
import com.github.epfl.meili.util.RecyclerViewInitializer

/**
 * To be implemented by all activities which display a list of posts
 * Performs basic initialization and sorting
 */
interface PostListActivity : ListSorter<Post> {

    var viewModel: PostListViewModel
    var showPOI: Boolean

    /**
     * Get intent to launch post activity
     */
    fun getPostActivityIntent(view: View): Intent {
        val postId: String = (view as TextView).text.toString()
        return Intent(getActivity(), PostActivity::class.java)
            .putExtra(Post.TAG, viewModel.getElements().value?.get(postId))
            .putExtra(PostActivity.POST_ID, postId)
    }

    /**
     * Basic Initialization of the activity's view model, recycler adapter, recycler view,
     * and a logged in listener for the voting feature
     */
    fun initActivity(
        viewModelClass: Class<out PostListViewModel>,
        recyclerView: RecyclerView,
        sortSpinner: Spinner
    ) {
        initViewModel(viewModelClass)
        initRecyclerAdapter(recyclerView)
        initLoggedInListener()
        initSorting(sortSpinner, R.array.sort_array)
    }

    fun initViewModel(viewModelClass: Class<out PostListViewModel>) {
        viewModel = ViewModelProvider(getActivity()).get(viewModelClass)
        viewModel.getElements().observe(getActivity()) {
            sortListener(it)
        }
    }

    private fun initRecyclerAdapter(recyclerView: RecyclerView) {
        recyclerAdapter = PostListRecyclerAdapter(viewModel, this, showPOI)
        RecyclerViewInitializer.initRecyclerView(
            recyclerAdapter,
            recyclerView,
            getActivity()
        )
    }

    fun initLoggedInListener() {
        Auth.isLoggedIn.observe(getActivity()) { loggedIn ->
            if (loggedIn && Auth.getCurrentUser() != null) {
                (recyclerAdapter as PostListRecyclerAdapter).submitUserInfo(Auth.getCurrentUser()!!.uid)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun orderList(list: List<Pair<String, Pair<Post, User>>>): List<Pair<String, Pair<Post, User>>> {
        return list.sortedBy { pair ->
            when (sortOrder) {
                NEWEST -> -pair.second.first.timestamp
                OLDEST -> pair.second.first.timestamp
                POPULAR -> (-(pair.second.first.upvoters.size - pair.second.first.downvoters.size)).toLong()
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun getAuthorUid(item: Post): String {
        return item.authorUid
    }
}
