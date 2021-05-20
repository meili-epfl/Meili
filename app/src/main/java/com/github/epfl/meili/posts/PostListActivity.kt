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
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.profile.UserProfileLinker
import com.github.epfl.meili.profile.friends.UserInfoService
import com.github.epfl.meili.util.RecyclerViewInitializer

/**
 * To be implemented by all activities which display a list of posts
 * Performs basic initialization and sorting
 */
interface PostListActivity : AdapterView.OnItemSelectedListener, UserProfileLinker<Post> {
    //todo: FIX ORDER OF POSTS (just add boolean)
    companion object {
        private const val NEWEST = "Newest"
        private const val OLDEST = "Oldest"
        private const val TAG = "PostListActivity"

        var serviceProvider: () -> UserInfoService = { UserInfoService() }
    }

    var viewModel: PostListViewModel

    fun getActivity(): AppCompatActivity

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
        initSorting(sortSpinner)
    }

    fun initViewModel(viewModelClass: Class<out PostListViewModel>) {
        viewModel = ViewModelProvider(getActivity()).get(viewModelClass)
        viewModel.getElements().observe(getActivity()) {
            postListener(it)
        }
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

    override fun onUsersInfoReceived(users: Map<String, User>, postMap: Map<String, Post>) {
        usersMap = HashMap(usersMap) + users
        val postsAndUsersMap = HashMap<String, Pair<Post, User>>()
        for ((postId, post) in postMap) {
            val user = usersMap[post.authorUid]
            if (user != null) {
                postsAndUsersMap[postId] = Pair(post, user)
            }
        }

        recyclerAdapter.submitList(postsAndUsersMap.toList())
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun postListener(postMap: Map<String, Post>) {
        val newUsers = ArrayList<String>()
        for ((postId, post) in postMap) {
            newUsers.add(post.authorUid)
        }

        serviceProvider().getUserInformation(newUsers, { onUsersInfoReceived(it, postMap) },
                { Log.d(TAG, "Error when fetching users information") })
    }

    private fun sortPosts(b: Boolean) {
        viewModel.getElements().removeObservers(getActivity())
        viewModel.getElements().observe(getActivity(), { map ->
            postListener(map.toList().sortedBy { pair ->
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