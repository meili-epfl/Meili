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
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.util.ImageUtility.compressAndUploadToFirebase
import com.github.epfl.meili.util.ImageUtility.getBitmapFromFilePath
import com.github.epfl.meili.util.MenuActivity
import com.github.epfl.meili.util.TopSpacingItemDecoration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ForumActivity : MenuActivity(R.menu.nav_forum_menu), AdapterView.OnItemSelectedListener  {
    companion object {
        private const val CARD_PADDING: Int = 30
    }

    private lateinit var recyclerAdapter: ForumRecyclerAdapter
    private lateinit var viewModel: ForumViewModel

    private lateinit var listPostsView: View
    private lateinit var createPostButton: ImageView

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
    private val launchGallery =  registerForActivityResult(ActivityResultContracts.GetContent()) { loadImage(it) }
    private lateinit var useCameraButton: ImageView
    private lateinit var useGalleryButton: ImageView
    private lateinit var displayImageView: ImageView
    private lateinit var executor: ExecutorService
    private var bitmap: Bitmap? = null

    private lateinit var poiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        executor = Executors.newSingleThreadExecutor()

        poiKey = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!.uid
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

        useCameraButton = findViewById(R.id.post_use_camera)
        useGalleryButton = findViewById(R.id.post_use_gallery)
        displayImageView = findViewById(R.id.post_display_image)
        filterSpinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this, R.array.filters_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            filterSpinner.adapter = adapter
        }
        filterSpinner.onItemSelectedListener = this

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
        executor.shutdown()
    }

    fun onForumButtonClick(view: View) {
        when (view) {
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
            .putExtra(MapActivity.POI_KEY, poiKey)
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

        val timeStamp = System.currentTimeMillis() / 1000

        viewModel.addElement(postId, Post(user.username, title, timeStamp, text))

        if (bitmap != null) {
            executor.execute { compressAndUploadToFirebase("images/forum/$postId", bitmap!!) }
        }

        showListPostsView()
    }

    private fun initViewModel() {
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
            if(loggedIn && Auth.getCurrentUser() != null){
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

    private fun sortPosts(b:Boolean){
            viewModel.getElements().observe(this, { map ->
                recyclerAdapter.submitList(map.toSortedMap(Comparator { lhs, rhs ->
                    //b is a switcher for most or least recent order
                    if(b){
                        // -1 - less than or equal, 1 - greater than, all inversed for descending
                        if (map[lhs]!!.timestamp >= map[rhs]!!.timestamp) -1 else  1}
                        else{if (map[lhs]!!.timestamp <= map[rhs]!!.timestamp) -1 else  1}
                }).toList()
                )
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
        val selectedItem = parent?.getItemAtPosition(pos)
        if(selectedItem=="Newest Post"){
            sortPosts(true);
        }
        if(selectedItem=="Oldest Post"){
            sortPosts(false);
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}