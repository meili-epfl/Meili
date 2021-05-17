package com.github.epfl.meili.review

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.UserProfileLinker
import com.github.epfl.meili.profile.friends.UserInfoService
import com.github.epfl.meili.util.*

class ReviewsActivity : MenuActivity(R.menu.nav_review_menu), ClickListener, UserProfileLinker<Review> {
    companion object {
        private const val CARD_PADDING: Int = 30

        private const val ADD_BUTTON_DRAWABLE = android.R.drawable.ic_input_add
        private const val EDIT_BUTTON_DRAWABLE = android.R.drawable.ic_menu_edit
        private const val TAG = "ReviewsActivity"

        var serviceProvider: () -> UserInfoService = { UserInfoService() }
    }

    private var currentUserReview: Review? = null

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

    override lateinit var recyclerAdapter: MeiliRecyclerAdapter<Pair<Review, User>>
    override lateinit var usersMap: Map<String, User>
    private lateinit var poi: PointOfInterest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        listReviewsView = findViewById(R.id.list_reviews)
        editReviewView = findViewById(R.id.edit_review)


        usersMap = HashMap()

        poi = intent.getParcelableExtra(MapActivity.POI_KEY)!!
        val poiKey = poi.uid
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
        UIUtility.hideSoftKeyboard(this)
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

        val userKey = Auth.getCurrentUser()!!.uid
        viewModel.addElement(userKey, Review(rating, title, summary))
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
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Review>

        viewModel.initDatabase(FirestoreDatabase("review/$poiKey/reviews", Review::class.java))
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

        val newUsersList = map.keys.toList().minus(usersMap.keys.toList())

        serviceProvider().getUserInformation(newUsersList, { onUsersInfoReceived(it, map) },
                { Log.d(TAG, "Error when fetching users information") })
        averageRatingView.text = getString(R.string.average_rating_format).format(Review.averageRating(map))
    }


    private fun initRecyclerView() {
        recyclerAdapter = ReviewsRecyclerAdapter(this)
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
}
