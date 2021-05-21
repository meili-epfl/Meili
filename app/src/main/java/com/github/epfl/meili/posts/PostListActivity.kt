package com.github.epfl.meili.posts

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.RecyclerViewInitializer

/**
 * To be implemented by all activities which display a list of posts
 * Performs basic initialization and sorting
 */
interface PostListActivity : AdapterView.OnItemSelectedListener {
    companion object {
        private const val NEWEST = "Newest"
        private const val OLDEST = "Oldest"
        private const val POPULAR= "Popularity"
    }

    var recyclerAdapter: PostListRecyclerAdapter
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
            recyclerAdapter.submitList(it.toList())
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerAdapter(recyclerView: RecyclerView) {
        recyclerAdapter = PostListRecyclerAdapter(viewModel)
        RecyclerViewInitializer.initRecyclerView(
            recyclerAdapter,
            recyclerView,
            getActivity()
        )
    }

    fun initLoggedInListener() {
        Auth.isLoggedIn.observe(getActivity(), { loggedIn ->
            if (loggedIn && Auth.getCurrentUser() != null) {
                recyclerAdapter.submitUserInfo(Auth.getCurrentUser()!!.uid)
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

    private fun sortPostsByTime(b: Boolean) {
        viewModel.getElements().removeObservers(getActivity())
        viewModel.getElements().observe(getActivity(), { map ->
            recyclerAdapter.submitList(map.toList().sortedBy { pair ->
                if (b)
                    -pair.second.timestamp
                else
                    pair.second.timestamp
            })
            recyclerAdapter.notifyDataSetChanged()
        })
    }

    private fun sortPostsByPopularity(){
        viewModel.getElements().removeObservers(getActivity())
        viewModel.getElements().observe(getActivity(), { map ->
            recyclerAdapter.submitList(map.toList().sortedBy { pair ->
                    -(pair.second.upvoters.size-pair.second.downvoters.size)
            })
            recyclerAdapter.notifyDataSetChanged()
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when (parent?.getItemAtPosition(pos)) {
            NEWEST -> sortPostsByTime(true)
            OLDEST -> sortPostsByTime(false)
            POPULAR-> sortPostsByPopularity()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}