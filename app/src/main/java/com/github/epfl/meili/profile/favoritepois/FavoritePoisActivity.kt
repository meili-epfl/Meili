package com.github.epfl.meili.profile.favoritepois

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.posts.forum.ForumActivity
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.RecyclerViewInitializer.initRecyclerView


class FavoritePoisActivity : AppCompatActivity() {
    companion object {
        private const val ACTIVITY_TITLE = "Favorite POIs"
         const val DB_PATH = "poi-favorite/%s/poi-favorite"
    }

    private val recyclerAdapter = FavoritePoisRecyclerAdapter()
    private lateinit var viewModel: MeiliViewModel<PointOfInterest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_pois)

        title = ACTIVITY_TITLE

        val userKey = Auth.getCurrentUser()!!.uid

        initViewModel(userKey)
        initRecyclerView(
            recyclerAdapter,
            findViewById(R.id.favorite_pois_recycler_view),
            this
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun onFavoritePoisButtonClick(view: View) {
        startActivity(
            Intent(this, ForumActivity::class.java).putExtra(
                MapActivity.POI_KEY,
                viewModel.getElements().value?.get((view.findViewById(R.id.poi_id) as TextView).text.toString())
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewModel(userKey: String) {
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<PointOfInterest>

        viewModel.initDatabase(
            FirestoreDatabase(
                String.format(DB_PATH, userKey),
                PointOfInterest::class.java
            )
        )
        viewModel.getElements().observe(this, { map ->
            favoritePoisMapListener(map)
        })
    }

    private fun favoritePoisMapListener(map: Map<String, PointOfInterest>) {
        recyclerAdapter.submitList(map.toList())
        recyclerAdapter.notifyDataSetChanged()
    }
}