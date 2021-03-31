package com.github.epfl.meili.review

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.util.TopSpacingItemDecoration


class ReviewsActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "ReviewsActivity"
        private const val CARD_PADDING: Int = 30

        private const val ADD_BUTTON = android.R.drawable.ic_input_add
        private const val EDIT_BUTTON = android.R.drawable.ic_menu_edit
    }

    private var currentUserReview: Review? = null

    private lateinit var reviewAdapter: ReviewRecyclerAdapter
    private lateinit var viewModel: ReviewsActivityViewModel

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

        val poiKey = intent.getStringExtra("POI_KEY")!!
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

        viewModel.addReview(Auth.getCurrentUser()!!.uid, Review(rating, title, summary))
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
        editTitleView = findViewById(R.id.edit_title)
        editSummaryView = findViewById(R.id.edit_summary)
        submitButton = findViewById(R.id.submit_review)
        cancelButton = findViewById(R.id.cancel_review)
    }

    private fun initViewModel(poiKey: String) {
        floatingActionButton = findViewById(R.id.fab_add_edit_review)
        averageRatingView = findViewById(R.id.average_rating)

        viewModel = ViewModelProvider(this).get(ReviewsActivityViewModel::class.java)

        viewModel.setReviewService(FirestoreReviewService(poiKey))
        viewModel.getReviews().observe(this, { map -> reviewsMapListener(map) })

        viewModel.getAverageRating().observe(this, {averageRating ->
            averageRatingView.text = getString(R.string.average_rating_format).format(averageRating)
        })
    }

    private fun reviewsMapListener(map: Map<String, Review>) {
        if (Auth.getCurrentUser() != null) {
            val uid = Auth.getCurrentUser()!!.uid
            if (map.containsKey(uid)) {
                currentUserReview = map[uid]
                floatingActionButton.setImageResource(EDIT_BUTTON)
            } else {
                currentUserReview = null
                floatingActionButton.setImageResource(ADD_BUTTON)
            }
        }

        reviewAdapter.submitList(map.toList())
        reviewAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        reviewAdapter = ReviewRecyclerAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReviewsActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = reviewAdapter
        }
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this, { loggedIn ->
            floatingActionButton.isEnabled = loggedIn
            if (loggedIn)
                floatingActionButton.visibility = View.VISIBLE
            else
                floatingActionButton.visibility = View.GONE
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
}
