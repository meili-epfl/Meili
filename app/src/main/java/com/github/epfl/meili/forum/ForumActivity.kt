package com.github.epfl.meili.forum

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.poi.PointOfInterest
import com.github.epfl.meili.review.ReviewsActivity
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration


class ForumActivity : AppCompatActivity() {
    companion object {
        private const val CARD_PADDING: Int = 30
        private const val FIRESTORE_PATH: String = "posts"
    }

    private lateinit var recyclerAdapter: ForumRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<Post>

    private lateinit var listPostsView: View
    private lateinit var createPostButton: ImageView

    private lateinit var editPostView: View
    private lateinit var editTitleView: EditText
    private lateinit var editTextVIew: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        initViews()
        initRecyclerView()
        initViewModel()
        initLoggedInListener()

        showListPostsView()
    }

    private fun initViews() {
        listPostsView = findViewById(R.id.list_posts)
        createPostButton = findViewById(R.id.create_post)

        editPostView = findViewById(R.id.edit_post)
        editTitleView = findViewById(R.id.post_edit_title)
        editTextVIew = findViewById(R.id.post_edit_text)
        submitButton = findViewById(R.id.submit_post)
        cancelButton = findViewById(R.id.cancel_post)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun onForumButtonClick(view: View) {
        when (view) {
            createPostButton -> {
                showEditPostView()
            }
            submitButton -> addPost()
            cancelButton -> showListPostsView()
            else -> openPost(view.findViewById(R.id.post_id))
        }
    }

    private fun openPost(view: View) {
        val postId: String = (view as TextView).text.toString()
        val intent: Intent = Intent(this, PostActivity::class.java)
                .putExtra("Post", viewModel.getElements().value?.get(postId))
        startActivity(intent)
    }

    private fun addPost() {
        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("Assertion failed")
        }

        val user: User = Auth.getCurrentUser()!!

        val postId = "${user.uid}${System.currentTimeMillis()}"

        val title = editTitleView.text.toString()
        val text = editTextVIew.text.toString()

        viewModel.addElement(postId, Post(user.username, title, text))

        showListPostsView()
    }

    private fun initViewModel() {
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Post>

        viewModel.setDatabase(FirestoreDatabase(FIRESTORE_PATH, Post::class.java))
        viewModel.getElements().observe(this, { map ->
            recyclerAdapter.submitList(map.toList())
            recyclerAdapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter = ForumRecyclerAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.forum_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ForumActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = recyclerAdapter
        }
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this, { loggedIn ->
            createPostButton.isEnabled = loggedIn
            createPostButton.visibility = if (loggedIn)
                View.VISIBLE
            else
                View.GONE
        })
    }

    private fun showEditPostView() {
        listPostsView.visibility = View.GONE
        editPostView.visibility = View.VISIBLE
    }

    private fun showListPostsView() {
        listPostsView.visibility = View.VISIBLE
        editPostView.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate menu to enable adding chat and review buttons on the top
        menuInflater.inflate(R.menu.nav_forum_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // get the POI
        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!
        //Now that the buttons are added at the top control what each menu buttons does
        val intent: Intent = when (item?.itemId) {
            R.id.menu_reviews_from_forum -> {
                Intent(this, ReviewsActivity::class.java)
                        .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_chat_from_forum-> {
                Intent(this, ChatLogActivity::class.java)
                        .putExtra(MapActivity.POI_KEY, poi)
            }
            else -> {
                Intent(this, ForumActivity::class.java)
            }
        }
        //clear the older intents so that the back button works correctly
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
}