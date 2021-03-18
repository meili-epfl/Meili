package com.github.epfl.meili.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.util.TopSpacingItemDecoration


class ReviewsActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "ReviewsActivity"
        private const val CARD_PADDING: Int = 30
    }

    private lateinit var reviewAdapter: ReviewRecyclerAdapter
    private lateinit var viewModel: ReviewsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        viewModel = ViewModelProvider(this).get(ReviewsActivityViewModel::class.java)
        // TODO viewModel.init(getPoiID())
        viewModel.getReviews().observe(this, Observer { reviews ->
            reviewAdapter.notifyDataSetChanged()

        })
        viewModel.getIsUpdating().observe(this, Observer { isUpdating ->
            // TODO process isUpdating
        })

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        reviewAdapter = ReviewRecyclerAdapter(viewModel.getReviews().value!!)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReviewsActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = reviewAdapter
        }
    }
}