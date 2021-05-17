package com.github.epfl.meili.posts

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration
import com.squareup.picasso.Picasso

class PostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PostActivity"
        private val DEFAULT_URI = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Forum_romanum_6k_%285760x2097%29.jpg/2880px-Forum_romanum_6k_%285760x2097%29.jpg")
        const val POST_ID = "Post_ID"
        private const val COMMENTS_PADDING: Int = 20
    }

    private lateinit var recyclerAdapter: CommentsRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<Comment>

    private lateinit var imageView: ImageView
    private lateinit var commentButton: Button
    private lateinit var editText: EditText
    private lateinit var addCommentButton : Button

    private lateinit var postId: String
    private lateinit var poiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post: Post = intent.getParcelableExtra(Post.TAG)!!
        postId = intent.getStringExtra(POST_ID)!!
        poiKey = intent.getStringExtra(MapActivity.POI_KEY)!!

        initViews(post)

        FirebaseStorageService.getDownloadUrl(
                "images/forum/$postId",
                { uri -> getDownloadUrlCallback(uri)},
                { exception ->
                    Log.e(TAG,"Image not found", exception)
                    getDownloadUrlCallback(DEFAULT_URI)
                }
        )

        initViewModel()
        initRecyclerView()
        initLoggedInListener()
    }

    private fun getDownloadUrlCallback(uri: Uri) {
        Picasso.get().load(uri).into(imageView)
    }

    private fun initViews(post: Post) {
        val authorView: TextView = findViewById(R.id.post_author)
        val titleView: TextView = findViewById(R.id.post_title)
        val textView: TextView = findViewById(R.id.post_text)
        authorView.text = post.author
        titleView.text = post.title
        textView.text = post.text

        imageView = findViewById(R.id.post_image)

        commentButton = findViewById(R.id.comment_button)
        editText = findViewById(R.id.edit_comment)
        addCommentButton = findViewById(R.id.add_comment)
        commentButton.setOnClickListener { showEditCommentView() }
        addCommentButton.setOnClickListener { addComment() }
    }

    private fun initViewModel() {
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Comment>

        viewModel.initDatabase(FirestoreDatabase("forum/$postId/comments", Comment::class.java))
        viewModel.getElements().observe(this, { map ->
            recyclerAdapter.submitList(map.toList())
            recyclerAdapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter = CommentsRecyclerAdapter(viewModel)
        val recyclerView: RecyclerView = findViewById(R.id.comments_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            addItemDecoration(TopSpacingItemDecoration(COMMENTS_PADDING))
            adapter = recyclerAdapter
        }
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this, { loggedIn ->
            val layout: LinearLayout = findViewById(R.id.new_comment_layout)
            layout.visibility = if (loggedIn)
                View.VISIBLE
            else
                View.INVISIBLE
        })
    }

    private fun showEditCommentView() {
        commentButton.visibility = View.INVISIBLE
        editText.visibility = View.VISIBLE
        addCommentButton.visibility = View.VISIBLE

        editText.text.clear()
    }

    private fun hideEditCommentView() {
        commentButton.visibility = View.VISIBLE
        editText.visibility = View.INVISIBLE
        addCommentButton.visibility = View.INVISIBLE
    }

    private fun addComment() {
        if (Auth.getCurrentUser() == null) {
            error("Unconnected user is trying to add comment")
        }
        val user: User = Auth.getCurrentUser()!!
        val commentId = "${user.uid}${System.currentTimeMillis()}"
        val text = editText.text.toString()

        viewModel.addElement(commentId, Comment(user.username, text))

        hideEditCommentView()
    }
}