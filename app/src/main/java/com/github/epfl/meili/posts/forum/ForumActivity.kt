package com.github.epfl.meili.posts.forum

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.poi.PointOfInterestStatus
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.posts.PostListActivity
import com.github.epfl.meili.posts.PostListViewModel
import com.github.epfl.meili.util.ImageSetter
import com.github.epfl.meili.util.ImageUtility.compressAndUploadToFirebase
import com.github.epfl.meili.util.ImageUtility.getBitmapFromFilePath
import com.github.epfl.meili.util.ListSorter.Companion.NEWEST
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.UIUtility
import com.github.epfl.meili.util.WritingPolicy
import com.github.epfl.meili.util.navigation.PoiActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ForumActivity : PoiActivity(R.layout.activity_forum, R.id.forum_activity), PostListActivity {
    override lateinit var recyclerAdapter: MeiliRecyclerAdapter<Pair<Post, User>>
    override lateinit var viewModel: PostListViewModel

    override var usersMap: Map<String, User> = HashMap()
    override var listMap: Map<String, Post> = HashMap()
    override var sortOrder = NEWEST
    override var showPOI = false

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
    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { loadImage(it) }

    private lateinit var useCameraButton: ImageView
    private lateinit var useGalleryButton: ImageView
    private lateinit var displayImageView: ImageView
    private lateinit var executor: ExecutorService
    private var bitmap: Bitmap? = null

    private lateinit var poi: PointOfInterest
    private lateinit var poiStatus: PointOfInterestStatus

    override fun getActivity(): AppCompatActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        executor = Executors.newSingleThreadExecutor()

        poi = intent.getParcelableExtra(MapActivity.POI_KEY)!!

        poiStatus = intent.getSerializableExtra(MapActivity.POI_STATUS_KEY) as PointOfInterestStatus

        supportActionBar?.title = poi.name

        initViews()

        initActivity(
            PostListViewModel::class.java,
            findViewById(R.id.forum_recycler_view),
            findViewById(R.id.sort_spinner)
        )

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

    fun onClick(view: View) {
        UIUtility.hideSoftKeyboard(this)
        when (view) {
            createPostButton -> showEditPostView()
            submitButton -> addPost()
            cancelButton -> showListPostsView()
            useGalleryButton -> launchGallery.launch("image/*")
            useCameraButton -> launchCameraActivity.launch(
                Intent(this, CameraActivity::class.java)
                    .putExtra(CameraActivity.EDIT_PHOTO, true)
            )
            else -> startActivity(getPostActivityIntent(view.findViewById(R.id.post_id)))
        }
    }

    private fun addPost() {
        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("Assertion failed")
        }

        val user: User = Auth.getCurrentUser()!!
        val timestamp = System.currentTimeMillis()

        val title = editTitleView.text.toString()
        val text = editTextVIew.text.toString()




        val post = Post(poi.uid, poi.name, user.uid, title, timestamp, text, bitmap != null)


        viewModel.addElement(post.postId(), post)

        if (post.hasPhoto) {
            executor.execute {
                compressAndUploadToFirebase(
                    ImageSetter.imagePostPath(post.postId()),
                    bitmap!!
                )
            }
        }

        showListPostsView()
    }

    override fun initViewModel(viewModelClass: Class<out PostListViewModel>) {
        super.initViewModel(viewModelClass)

        viewModel.initDatabase(AtomicPostFirestoreDatabase("forum") {
            it.whereEqualTo(Post.POI_KEY_FIELD, poi.uid)
        })
    }

    override fun initLoggedInListener() {
        super.initLoggedInListener()

        Auth.isLoggedIn.observe(this) { loggedIn ->
            val isWriteEnabled = WritingPolicy.isWriteEnabled(loggedIn, poiStatus)
            createPostButton.isEnabled = isWriteEnabled
            createPostButton.isVisible = isWriteEnabled
        }
    }

    private fun showEditPostView() {
        listPostsView.visibility = View.GONE
        editPostView.visibility = View.VISIBLE

        // Clear fields and photo
        editTitleView.text.clear()
        editTextVIew.text.clear()
        bitmap = null
        displayImageView.setImageBitmap(bitmap)
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