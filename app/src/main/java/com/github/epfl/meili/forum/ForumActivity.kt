package com.github.epfl.meili.forum

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.util.ImageUtility.compressAndUploadToFirebase
import com.github.epfl.meili.util.ImageUtility.getBitmapFromFilePath
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.MenuActivity
import com.github.epfl.meili.util.TopSpacingItemDecoration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ForumActivity : MenuActivity(R.menu.nav_forum_menu) {
    companion object {
        private const val CARD_PADDING: Int = 30
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

    // image choice and upload
    private val launchCameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null && result.data!!.data != null) {
                loadImage(result.data!!.data!!)
            }
        }
    private val launchGallery =  registerForActivityResult(ActivityResultContracts.GetContent()) { loadImage(it) }
    private lateinit var useCameraButton: ImageView
    private lateinit var useGalleryButton: ImageView
    private lateinit var displayImageView: ImageView
    private lateinit var executor: ExecutorService
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        executor = Executors.newSingleThreadExecutor()

        val poiKey = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!.uid
        initViews()
        initRecyclerView()
        initViewModel(poiKey)
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

        useCameraButton = findViewById(R.id.post_use_camera)
        useGalleryButton = findViewById(R.id.post_use_gallery)
        displayImageView = findViewById(R.id.post_display_image)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
        executor.shutdown()
    }

    fun onForumButtonClick(view: View) {
        when(view) {
            createPostButton -> showEditPostView()
            submitButton -> addPost()
            cancelButton -> showListPostsView()
            useGalleryButton -> launchGallery.launch("image/*")
            useCameraButton -> launchCameraActivity.launch(Intent(this, CameraActivity::class.java))
            else -> openPost(view.findViewById(R.id.post_id))
        }
    }

    private fun openPost(view: View) {
        val postId: String = (view as TextView).text.toString()
        val intent: Intent = Intent(this, PostActivity::class.java)
                .putExtra(Post.TAG, viewModel.getElements().value?.get(postId))
                .putExtra(PostActivity.POST_ID, postId)
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

        if (bitmap != null) {
            executor.execute { compressAndUploadToFirebase("forum/$postId", bitmap!!) }
        }

        showListPostsView()
    }

    private fun initViewModel(poiKey: String) {
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Post>

        viewModel.setDatabase(FirestoreDatabase("forum/$poiKey/posts", Post::class.java))
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

    private fun loadImage(filePath: Uri) {
        executor.execute {
            val bitmap = getBitmapFromFilePath(contentResolver, filePath)

            runOnUiThread {
                this.bitmap = bitmap
                displayImageView.setImageBitmap(bitmap)
            }
        }
    }
}