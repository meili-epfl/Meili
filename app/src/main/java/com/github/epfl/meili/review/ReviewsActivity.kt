package com.github.epfl.meili.review

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.poi.PointOfInterestStatus
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.UserProfileLinker
import com.github.epfl.meili.profile.friends.UserInfoService
import com.github.epfl.meili.util.*
import com.github.epfl.meili.util.RecyclerViewInitializer.initRecyclerView
import com.github.epfl.meili.util.navigation.PoiActivity

class ReviewsActivity : PoiActivity(R.layout.activity_reviews, R.id.reviews_activity),
    ClickListener,
    UserProfileLinker<Review> {
    companion object {
        private const val ADD_BUTTON_DRAWABLE = android.R.drawable.ic_input_add
        private const val EDIT_BUTTON_DRAWABLE = android.R.drawable.ic_menu_edit

        var serviceProvider: () -> UserInfoService = { UserInfoService() }
    }

    private var currentUserReview: Review? = null

    private lateinit var viewModel: MeiliViewModel<Review>

    private lateinit var listReviewsView: View
    private lateinit var editReviewView: View

    private lateinit var floatingActionButton: ImageView
    private lateinit var averageRatingView: TextView
    private lateinit var averageRatingBarView: RatingBar

    private lateinit var ratingBar: RatingBar
    private lateinit var editTitleView: EditText
    private lateinit var editSummaryView: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override lateinit var recyclerAdapter: MeiliRecyclerAdapter<Pair<Review, User>>
    override lateinit var usersMap: Map<String, User>
    private lateinit var poi: PointOfInterest
    private lateinit var poiStatus: PointOfInterestStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listReviewsView = findViewById(R.id.list_reviews)
        editReviewView = findViewById(R.id.edit_review)

        usersMap = HashMap()

        poi = intent.getParcelableExtra(MapActivity.POI_KEY)!!
        poiStatus = intent.getSerializableExtra(MapActivity.POI_STATUS_KEY) as PointOfInterestStatus

        supportActionBar?.title = poi.name

        val poiKey = poi.uid
        showListReviewsView()
        initReviewEditView()
        recyclerAdapter = ReviewsRecyclerAdapter(this)
        initRecyclerView(
            recyclerAdapter,
            findViewById(R.id.reviews_recycler_view),
            this
        )
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

        val currentUserUid = Auth.getCurrentUser()!!.uid
        val key = currentUserUid + poi.uid
        viewModel.addElement(key, Review(currentUserUid, poi.uid, rating, title, summary))
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
        averageRatingBarView = findViewById(R.id.average_ratingbar)

        @Suppress("UNCHECKED_CAST")
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Review>

        viewModel.initDatabase(FirestoreDatabase("reviews", Review::class.java) {
            it.whereEqualTo(Review.POI_KEY_FIELD, poiKey)
        })

        viewModel.getElements().observe(this) { reviewsMapListener(it) }
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

        serviceProvider().getUserInformation(map.values.map { it.authorUid }) {
            onUsersInfoReceived(it, map)
        }

        // Show average rating
        val rating = Review.averageRating(map)
        averageRatingView.text = getString(R.string.average_rating_format).format(rating)
        averageRatingBarView.rating = rating
    }

    override fun onUsersInfoReceived(users: Map<String, User>, map: Map<String, Review>) {
        usersMap = HashMap(usersMap) + users

        val reviewsAndUsersMap = HashMap<String, Pair<Review, User>>()
        for ((postId, review) in map) {
            val user = usersMap[review.authorUid]
            if (user != null) {
                reviewsAndUsersMap[postId] = Pair(review, user)
            }
        }

        recyclerAdapter.submitList(reviewsAndUsersMap.toList())
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this) { loggedIn ->
            val isWriteEnabled = WritingPolicy.isWriteEnabled(loggedIn, poiStatus)
            floatingActionButton.isEnabled = isWriteEnabled
            floatingActionButton.isVisible = isWriteEnabled
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