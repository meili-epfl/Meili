package com.github.epfl.meili.review

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
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration
import com.github.epfl.meili.poi.PointOfInterest
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration


class ReviewsActivity : AppCompatActivity() {
    companion object {
        private const val CARD_PADDING: Int = 30

        private const val ADD_BUTTON_DRAWABLE = android.R.drawable.ic_input_add
        private const val EDIT_BUTTON_DRAWABLE = android.R.drawable.ic_menu_edit
    }

    private var currentUserReview: Review? = null

    private lateinit var recyclerAdapter: ReviewsRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<Review>

    private lateinit var listReviewsView: View
    private lateinit var editReviewView: View

    private lateinit var floatingActionButton: ImageView
    private lateinit var averageRatingView: TextView

    private lateinit var ratingBar: RatingBar
    private lateinit var editTitleView: EditText
    private lateinit var editSummaryView: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        listReviewsView = findViewById(R.id.list_reviews)
        editReviewView = findViewById(R.id.edit_review)


        //take the entire poi even if only the name is needed in case you want to go to chat acitivty directly
        val poiKey = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!.name
        showListReviewsView()
        initReviewEditView()
        initRecyclerView()
        initViewModel(poiKey)
        initLoggedInListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun onReviewButtonClick(view: View) {
        when (view.id) {
            R.id.submit_review -> {
                submitButtonListener()
                showListReviewsView()
            }
            R.id.fab_add_edit_review -> {
                editReviewButtonListener()
                showEditReviewView()
            }
            R.id.cancel_review -> {
                showListReviewsView()
            }
        }
    }

    private fun submitButtonListener() {
        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("Assertion failed")
        }

        val rating = ratingBar.rating
        val title = editTitleView.text.toString()
        val summary = editSummaryView.text.toString()

        viewModel.addElement(Auth.getCurrentUser()!!.uid, Review(rating, title, summary))
    }

    private fun editReviewButtonListener() {
        if (currentUserReview != null) {
            val review = currentUserReview!!
            ratingBar.rating = review.rating
            editTitleView.setText(review.title)
            editSummaryView.setText(review.summary)
        }
    }

    private fun initReviewEditView() {
        ratingBar = findViewById(R.id.rating_bar)
        editTitleView = findViewById(R.id.review_edit_title)
        editSummaryView = findViewById(R.id.review_edit_summary)
        submitButton = findViewById(R.id.submit_review)
        cancelButton = findViewById(R.id.cancel_review)
    }

    private fun initViewModel(poiKey: String) {
        floatingActionButton = findViewById(R.id.fab_add_edit_review)
        averageRatingView = findViewById(R.id.average_rating)

        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Review>

        viewModel.setDatabase(FirestoreDatabase(poiKey, Review::class.java))
        viewModel.getElements().observe(this, { map ->
            reviewsMapListener(map)
        })
    }

    private fun reviewsMapListener(map: Map<String, Review>) {
        if (Auth.getCurrentUser() != null) {
            val uid = Auth.getCurrentUser()!!.uid
            if (map.containsKey(uid)) {
                currentUserReview = map[uid]
                floatingActionButton.setImageResource(EDIT_BUTTON_DRAWABLE)
            } else {
                currentUserReview = null
                floatingActionButton.setImageResource(ADD_BUTTON_DRAWABLE)
            }
        }

        averageRatingView.text = getString(R.string.average_rating_format).format(Review.averageRating(map))
        recyclerAdapter.submitList(map.toList())
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        recyclerAdapter = ReviewsRecyclerAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.reviews_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReviewsActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = recyclerAdapter
        }
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this, { loggedIn ->
            floatingActionButton.isEnabled = loggedIn
            floatingActionButton.visibility = if (loggedIn)
                View.VISIBLE
            else
                View.GONE
        })
    }

    private fun showEditReviewView() {
        listReviewsView.visibility = View.GONE
        editReviewView.visibility = View.VISIBLE
    }

    private fun showListReviewsView() {
        listReviewsView.visibility = View.VISIBLE
        editReviewView.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate menu to enable adding chat and review buttons on the top
        menuInflater.inflate(R.menu.nav_review_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // get the POI
        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!
        //Now that the buttons are added at the top control what each menu buttons does
        val intent: Intent = when (item?.itemId) {
            R.id.menu_chat_from_review -> {
                Intent(this, ChatLogActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_forum_from_review-> {
                Intent(this, ForumActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            else -> {
                Intent(this, ReviewsActivity::class.java)
            }
        }
        //clear the older intents so that the back button works correctly
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
}
