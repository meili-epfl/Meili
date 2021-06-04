package com.github.epfl.meili.posts

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.*
import com.github.epfl.meili.util.ListSorter.Companion.NEWEST
import com.github.epfl.meili.util.ListSorter.Companion.OLDEST
import com.github.epfl.meili.util.ListSorter.Companion.serviceProvider
import com.github.epfl.meili.util.RecyclerViewInitializer.initRecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class PostActivity : AppCompatActivity(), ClickListener, ListSorter<Comment> {
    companion object {
        const val POST_ID = "Post_ID"
    }

    override lateinit var recyclerAdapter: MeiliRecyclerAdapter<Pair<Comment, User>>
    override var usersMap: Map<String, User> = HashMap()

    private lateinit var viewModel: MeiliViewModel<Comment>

    private lateinit var imageView: ImageView
    private lateinit var commentButton: Button
    private lateinit var editText: EditText
    private lateinit var addCommentButton: Button

    private lateinit var postId: String
    private lateinit var post: Post

    override var listMap: Map<String, Comment> = HashMap()
    override var sortOrder = NEWEST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post: Post = intent.getParcelableExtra(Post.TAG)!!
        postId = intent.getStringExtra(POST_ID)!!

        initViews(post)

        if (post.hasPhoto) {
            ImageSetter.setImageInto(postId, imageView, ImageSetter.imagePostPath)
        }

        initViewModel()
        initRecyclerAdapter()
        initLoggedInListener()
        initSorting(findViewById(R.id.sort_spinner), R.array.sort_array_comments)

        findViewById<TextView>(R.id.userName).setOnClickListener { openUserProfile(post.authorUid) }
        findViewById<CircleImageView>(R.id.userImage).setOnClickListener { openUserProfile(post.authorUid) }
    }

    private fun initViews(post: Post) {
        this.post = post
        val titleView: TextView = findViewById(R.id.post_title)
        val textView: TextView = findViewById(R.id.post_text)
        titleView.text = post.title
        textView.text = post.text

        imageView = findViewById(R.id.post_image)
        commentButton = findViewById(R.id.comment_button)
        editText = findViewById(R.id.edit_comment)
        addCommentButton = findViewById(R.id.add_comment)
        commentButton.setOnClickListener { showEditCommentView() }
        addCommentButton.setOnClickListener { addComment() }

        serviceProvider().getUserInformation(listOf(post.authorUid)) { onAuthorInfoReceived(it) }
    }

    private fun onAuthorInfoReceived(users: Map<String, User>) {
        val author = users[post.authorUid]
        val authorView: TextView = findViewById(R.id.userName)
        val imageAuthor: CircleImageView = findViewById(R.id.userImage)

        authorView.text = author?.username
        ImageSetter.setImageInto(author!!.uid, imageAuthor, ImageSetter.imageAvatarPath)
    }

    private fun initViewModel() {
        @Suppress("UNCHECKED_CAST")
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Comment>

        viewModel.initDatabase(FirestoreDatabase("forum/$postId/comments", Comment::class.java))
        viewModel.getElements().observe(this) { sortListener(it) }
    }

    private fun initRecyclerAdapter() {
        recyclerAdapter = CommentsRecyclerAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.comments_recycler_view)
        initRecyclerView(recyclerAdapter, recyclerView, this)
        ViewCompat.setNestedScrollingEnabled(recyclerView, false)
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this) { loggedIn ->
            val layout: LinearLayout = findViewById(R.id.new_comment_layout)
            layout.visibility = if (loggedIn)
                View.VISIBLE
            else
                View.INVISIBLE
        }
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
        val timestamp = System.currentTimeMillis()
        val commentId = "${user.uid}${timestamp}"
        val text = editText.text.toString()

        viewModel.addElement(commentId, Comment(user.uid, text, timestamp))

        hideEditCommentView()
    }

    override fun orderList(list: List<Pair<String, Pair<Comment, User>>>): List<Pair<String, Pair<Comment, User>>> {
        return list.sortedBy { pair ->
            when (sortOrder) {
                NEWEST -> -pair.second.first.timestamp
                OLDEST -> pair.second.first.timestamp
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun getAuthorUid(item: Comment): String {
        return item.authorUid
    }

    override fun getActivity(): AppCompatActivity {
        return this
    }
}