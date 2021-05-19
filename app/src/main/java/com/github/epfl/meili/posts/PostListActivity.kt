package com.github.epfl.meili.posts

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.posts.forum.ForumActivity
import com.github.epfl.meili.profile.UserProfileLinker
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.RecyclerViewInitializer

/**
 * To be implemented by all activities which display a list of posts
 * Performs basic initialization and sorting
 */
interface PostListActivity : AdapterView.OnItemSelectedListener, UserProfileLinker<Post> {
    companion object {
        private const val NEWEST = "Newest"
        private const val OLDEST = "Oldest"
        private const val TAG = "PostListsActivity"
    }

    override var recyclerAdapter: MeiliRecyclerAdapter<Pair<Post, User>>
    var viewModel: PostListViewModel

    fun getActivity(): AppCompatActivity

    /**
     * Get intent to launch post activity
     */
    fun getPostActivityIntent(view: View): Intent {
        Log.d(TAG, view.toString())
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
        initSorting(sortSpinner)
    }

    fun initViewModel(viewModelClass: Class<out PostListViewModel>) {
        viewModel = ViewModelProvider(getActivity()).get(viewModelClass)

        viewModel.getElements().observe(getActivity(), { map ->
            postsMapListener(map)
        })
    }

    private fun initRecyclerAdapter(recyclerView: RecyclerView) {
        recyclerAdapter = PostListRecyclerAdapter(viewModel, this)
        RecyclerViewInitializer.initRecyclerView(
                recyclerAdapter,
                recyclerView,
                getActivity()
        )
    }

    fun initLoggedInListener() {
        Auth.isLoggedIn.observe(getActivity(), { loggedIn ->
            if (loggedIn && Auth.getCurrentUser() != null) {
                (recyclerAdapter as PostListRecyclerAdapter).submitUserInfo(Auth.getCurrentUser()!!.uid)
                recyclerAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initSorting(sortSpinner: Spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                getActivity(), R.array.sort_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            sortSpinner.adapter = adapter
        }
        sortSpinner.onItemSelectedListener = this
    }


    private fun postsMapListener(map: Map<String, Post>) {
        Log.d(TAG, map.toString())

        val userUidToPost = HashMap<String, Post>()
        val userIds = ArrayList<String>()
        for ((key, value) in map) {
            userIds.add(value.authorUid)
            userUidToPost[value.authorUid] = value
        }

        val newUsersList = userIds.minus(usersMap.keys.toList())

        ForumActivity.serviceProvider().getUserInformation(newUsersList, { onUsersInfoReceived(it, userUidToPost) },
                { Log.d(TAG, "Error when fetching users information") })
    }

    private fun sortPosts(b: Boolean) {
        viewModel.getElements().removeObservers(getActivity())
        viewModel.getElements().observe(getActivity(), { map ->
            postsMapListener(map.toList().sortedBy { pair ->
                if (b)
                    -pair.second.timestamp
                else
                    pair.second.timestamp
            }.toMap())
        })
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when (parent?.getItemAtPosition(pos)) {
            NEWEST -> sortPosts(true)
            OLDEST -> sortPosts(false)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}