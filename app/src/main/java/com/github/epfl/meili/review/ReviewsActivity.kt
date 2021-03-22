package com.github.epfl.meili.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review
import com.github.epfl.meili.util.TopSpacingItemDecoration


class ReviewsActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "ReviewsActivity"
        private const val CARD_PADDING: Int = 30
    }

    private lateinit var reviewAdapter: ReviewRecyclerAdapter
    private lateinit var viewModel: ReviewsActivityViewModel
    private lateinit var viewModelFactory: ReviewsActivityViewModelFactory
    private lateinit var service: ReviewService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        initRecyclerView()

        service = FirestoreReviewSerivce("ChIJN1t_tDeuEmsRUsoyG83frY4") // TODO remove default poi key
        viewModelFactory = ReviewsActivityViewModelFactory(service)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ReviewsActivityViewModel::class.java)

        viewModel.getReviews().observe(this, Observer {list ->
            reviewAdapter.submitList(list)
            reviewAdapter.notifyDataSetChanged()
        })

        viewModel.addReview(Review(2, "beach too sandy", "water too wet"))
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
}