package com.github.epfl.meili.posts.forum

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
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.posts.PostActivity
import com.github.epfl.meili.posts.PostListRecyclerAdapter
import com.github.epfl.meili.profile.favoritepois.FavoritePoisActivity
import com.github.epfl.meili.util.ImageUtility.compressAndUploadToFirebase
import com.github.epfl.meili.util.ImageUtility.getBitmapFromFilePath
import com.github.epfl.meili.util.MenuActivity
import com.github.epfl.meili.util.TopSpacingItemDecoration
import com.github.epfl.meili.util.UIUtility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ForumActivity : MenuActivity(R.menu.nav_forum_menu), AdapterView.OnItemSelectedListener {
    companion object {
        private const val CARD_PADDING: Int = 30
        private const val NEWEST = "Newest"
        private const val OLDEST = "Oldest"
    }

    private lateinit var recyclerAdapter: PostListRecyclerAdapter
    private lateinit var viewModel: ForumViewModel

    private lateinit var listPostsView: View
    private lateinit var createPostButton: ImageView

    private lateinit var favoriteButton: FloatingActionButton
    private lateinit var editPostView: View
    private lateinit var editTitleView: EditText
    private lateinit var editTextVIew: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var filterSpinner: Spinner

    // image choice and upload
    private val launchCameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null && result.data!!.data != null) {
                loadImage(result.data!!.data!!)
            }
        }
    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { loadImage(it) }
    private lateinit var useCameraButton: ImageView
    private lateinit var useGalleryButton: ImageView
    private lateinit var displayImageView: ImageView
    private lateinit var executor: ExecutorService
    private var bitmap: Bitmap? = null

    private lateinit var poi: PointOfInterest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        executor = Executors.newSingleThreadExecutor()

        poi = intent.getParcelableExtra(MapActivity.POI_KEY)!!

        initViews()
        initViewModel()
        initRecyclerView()
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

        favoriteButton = findViewById(R.id.favorite)
        useCameraButton = findViewById(R.id.post_use_camera)
        useGalleryButton = findViewById(R.id.post_use_gallery)
        displayImageView = findViewById(R.id.post_display_image)
        filterSpinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this, R.array.sort_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            filterSpinner.adapter = adapter
        }
        filterSpinner.onItemSelectedListener = this

        if (Auth.getCurrentUser() == null) {
            favoriteButton.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
        executor.shutdown()
    }

    fun onForumButtonClick(view: View) {
        UIUtility.hideSoftKeyboard(this)
        when (view) {
            createPostButton -> showEditPostView()
            favoriteButton -> viewModel.addFavoritePoi(poi)
            submitButton -> addPost()
            cancelButton -> showListPostsView()
            useGalleryButton -> launchGallery.launch("image/*")
            useCameraButton -> launchCameraActivity.launch(
                Intent(this, CameraActivity::class.java)
                    .putExtra(CameraActivity.EDIT_PHOTO, true)
            )
            else -> openPost(view.findViewById(R.id.post_id))
        }
    }

    private fun openPost(view: View) {
        val postId: String = (view as TextView).text.toString()
        val intent: Intent = Intent(this, PostActivity::class.java)
            .putExtra(Post.TAG, viewModel.getElements().value?.get(postId))
            .putExtra(PostActivity.POST_ID, postId)
            .putExtra(MapActivity.POI_KEY, poi.uid)
        startActivity(intent)
    }

    private fun addPost() {
        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("Assertion failed")
        }

        val user: User = Auth.getCurrentUser()!!
        val timestamp = System.currentTimeMillis()

        val postId = "${user.uid}${timestamp}"

        val title = editTitleView.text.toString()
        val text = editTextVIew.text.toString()

        viewModel.addElement(postId, Post(poi.uid, user.username, title, timestamp, text))

        if (bitmap != null) {
            executor.execute { compressAndUploadToFirebase("images/forum/$postId", bitmap!!) }
        }

        showListPostsView()
    }

    private fun initViewModel() {
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this).get(ForumViewModel::class.java)
        viewModel.initDatabase(AtomicPostFirestoreDatabase("forum") {
            it.whereEqualTo(Post.POI_KEY_FIELD, poi.uid)
        })
        if (Auth.getCurrentUser() != null) {
            viewModel.initFavoritePoisDatabase(
                FirestoreDatabase( // add to poi favorites
                    String.format(FavoritePoisActivity.DB_PATH, Auth.getCurrentUser()!!.uid),
                    PointOfInterest::class.java
                )
            )
        }
        viewModel.getElements().observe(this, { map ->
            recyclerAdapter.submitList(map.toList())
            recyclerAdapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter = PostListRecyclerAdapter(viewModel)
        val recyclerView: RecyclerView = findViewById(R.id.forum_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ForumActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = recyclerAdapter
        }
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this, { loggedIn ->
            //If the user is logged in he can create a new post
            createPostButton.isEnabled = loggedIn
            createPostButton.visibility = if (loggedIn)
                View.VISIBLE
            else
                View.GONE

            //and upvote/downvote
            if (loggedIn && Auth.getCurrentUser() != null) {
                recyclerAdapter.submitUserInfo(Auth.getCurrentUser()!!.uid)
                recyclerAdapter.notifyDataSetChanged()
            }
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

    private fun sortPosts(b: Boolean) {
        viewModel.getElements().removeObservers(this)
        viewModel.getElements().observe(this, { map ->
            recyclerAdapter.submitList(map.toList().sortedBy { pair ->
                if (b)
                    -pair.second.timestamp
                else
                    pair.second.timestamp
            })
            recyclerAdapter.notifyDataSetChanged()
        })
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when (parent?.getItemAtPosition(pos)) {
            NEWEST -> sortPosts(true)
            OLDEST -> sortPosts(false)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}