package com.github.epfl.meili.review

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.util.TopSpacingItemDecoration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ReviewsActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "ReviewsActivity"
        private const val CARD_PADDING: Int = 30

        private const val ADD_BUTTON = android.R.drawable.ic_menu_add
        private const val EDIT_BUTTON = android.R.drawable.ic_menu_edit
    }

    private var currentUserReview: Review? = null

    private lateinit var reviewAdapter: ReviewRecyclerAdapter
    private lateinit var viewModel: ReviewsActivityViewModel

    private lateinit var listReviewsView: View
    private lateinit var editReviewView: View

    private lateinit var floatingActionButton: ImageView
    private lateinit var averageRatingBar: RatingBar
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

        initReviewEditView()
        showListReviewsView()

//        val poiKey = intent.getStringExtra("POI_KEY")!!

        initRecyclerView()
        initViewModel("asdnja") // TODO
    }


    private fun initReviewEditView() {
        ratingBar = findViewById(R.id.rating_bar)
        editTitleView = findViewById(R.id.edit_title)
        editSummaryView = findViewById(R.id.edit_summary)
        submitButton = findViewById(R.id.submit_review)
        cancelButton = findViewById(R.id.cancel_review)
    }

    fun submitButtonListener(view: View) {
        val rating = ratingBar.rating
        val title = editTitleView.text.toString()
        val summary = editSummaryView.text.toString()

        viewModel.addReview(Review(rating, title, summary))
        showListReviewsView()
    }

    fun cancelButtonListener(view: View) {
        showListReviewsView()
    }

    fun editReviewButtonListener(view: View) {
        if (BuildConfig.DEBUG && currentUserReview == null) {
            error("Assertion failed")
        }

        val review: Review = currentUserReview!!
        ratingBar.rating = review.rating
        editTitleView.setText(review.title)
        editSummaryView.setText(review.summary)
        showEditReviewView()
    }

    private fun initViewModel(poiKey: String) {
        floatingActionButton = findViewById(R.id.fab)
        averageRatingBar = findViewById(R.id.average_rating_bar)
        averageRatingView = findViewById(R.id.average_rating)

        viewModel = ViewModelProvider(this).get(ReviewsActivityViewModel::class.java)

        viewModel.setReviewService(FirestoreReviewService(poiKey))
        viewModel.getReviews().observe(this, Observer {map ->
            if (map.containsKey(Firebase.auth.uid!!)) {
                currentUserReview = map[Firebase.auth.uid!!]
                floatingActionButton.setImageResource(EDIT_BUTTON)
            } else {
                currentUserReview = null
                floatingActionButton.setImageResource(ADD_BUTTON)
            }
            reviewAdapter.submitList(map.toList())
            reviewAdapter.notifyDataSetChanged()
        })

        viewModel.getAverageRating().observe(this, {averageRating ->
            averageRatingBar.rating = averageRating
            averageRatingView.text = averageRating.toString()
        })
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

    private fun showEditReviewView() {
        listReviewsView.visibility = View.GONE
        editReviewView.visibility = View.VISIBLE
    }

    private fun showListReviewsView() {
        listReviewsView.visibility = View.VISIBLE
        editReviewView.visibility = View.GONE
    }
}
