package com.github.epfl.meili.profile.myposts

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.posts.PostListActivity
import com.github.epfl.meili.posts.PostListRecyclerAdapter
import com.github.epfl.meili.posts.PostListViewModel
import com.github.epfl.meili.profile.ProfileActivity

class MyPostsActivity : AppCompatActivity(), PostListActivity {
    override lateinit var recyclerAdapter: PostListRecyclerAdapter
    override lateinit var viewModel: PostListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        val userKey = intent.getStringExtra(ProfileActivity.USER_KEY)

        initActivity(
            PostListViewModel::class.java,
            findViewById(R.id.my_posts_recycler_view),
            findViewById(R.id.sort_spinner)
        )

        viewModel.initDatabase(AtomicPostFirestoreDatabase("forum") {
            it.whereEqualTo(Post.AUTHOR_UID_FIELD, userKey)
        })
    }

    override fun getActivity(): AppCompatActivity = this

    fun onClick(view: View) {
        startActivity(getPostActivityIntent(view.findViewById(R.id.post_id)))
    }
}