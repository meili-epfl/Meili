package com.github.epfl.meili.forum

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration
import com.squareup.picasso.Picasso

class PostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PostActivity"
        private val DEFAULT_URI = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Forum_romanum_6k_%285760x2097%29.jpg/2880px-Forum_romanum_6k_%285760x2097%29.jpg")
        const val POST_ID = "Post_ID"
        private const val COMMENTS_PADDING: Int = 40
    }

    private lateinit var recyclerAdapter: ForumRecyclerAdapter
    private lateinit var viewModel: ForumViewModel

    private lateinit var imageView: ImageView
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post: Post = intent.getParcelableExtra(Post.TAG)!!
        postId = intent.getStringExtra(POST_ID)!!

        val authorView: TextView = findViewById(R.id.post_author)
        val titleView: TextView = findViewById(R.id.post_title)
        val textView: TextView = findViewById(R.id.post_text)
        imageView = findViewById(R.id.post_image)

        authorView.text = post.author
        titleView.text = post.title
        textView.text = post.text

        FirebaseStorageService.getDownloadUrl(
                "images/forum/$postId",
                { uri -> getDownloadUrlCallback(uri)},
                { exception ->
                    Log.e(TAG,"Image not found", exception)
                    getDownloadUrlCallback(DEFAULT_URI)
                }
        )

        initViewModel("ChIJAAAAAAAAAAARg4pb6XR5bo0")
        initRecyclerView()
    }

    private fun getDownloadUrlCallback(uri: Uri) {
        Picasso.get().load(uri).into(imageView)
    }

    private fun initViewModel(poiKey: String) {
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this).get(ForumViewModel::class.java)

        viewModel.initDatabase(AtomicPostFirestoreDatabase("forum/$poiKey/posts"))
        viewModel.getElements().observe(this, { map ->
            recyclerAdapter.submitList(map.toList())
            recyclerAdapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter = ForumRecyclerAdapter(viewModel)
        val recyclerView: RecyclerView = findViewById(R.id.comments_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            addItemDecoration(TopSpacingItemDecoration(COMMENTS_PADDING))
            adapter = recyclerAdapter
        }
    }
}