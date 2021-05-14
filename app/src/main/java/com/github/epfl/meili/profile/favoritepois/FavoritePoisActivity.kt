package com.github.epfl.meili.profile.favoritepois

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.FavoritePointOfInterest
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration
import java.util.*


class FavoritePoisActivity : AppCompatActivity() {
    companion object {
        private const val CARD_PADDING: Int = 30
        private const val ACTIVITY_TITLE = "Favorite POIs"
        private const val DB_PATH = "poi-favorite/%s/poi-favorite"

        fun addPoiToFavorites(userKey: String, poi: PointOfInterest) {
            FirestoreDatabase( // add to poi favorites
                String.format(DB_PATH, userKey),
                FavoritePointOfInterest::class.java
            ).addElement(poi.uid, FavoritePointOfInterest(poi))
        }
    }

    private lateinit var radapter: FavoritePoisRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<FavoritePointOfInterest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_pois)

        title = ACTIVITY_TITLE

        val userKey = Auth.getCurrentUser()!!.uid
        initRecyclerView()
        initViewModel(userKey)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun onFavoritePoisButtonClick(view: View) {
        startActivity(
            Intent(this, ForumActivity::class.java).putExtra(
                MapActivity.POI_KEY,
                viewModel.getElements().value?.get((view.findViewById(R.id.poi_id) as TextView).text.toString())?.poi
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewModel(userKey: String) {
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<FavoritePointOfInterest>

        viewModel.initDatabase(
            FirestoreDatabase(
                String.format(DB_PATH, userKey),
                FavoritePointOfInterest::class.java
            )
        )
        viewModel.getElements().observe(this, { map ->
            favoritePoisMapListener(map)
        })
    }

    private fun favoritePoisMapListener(map: Map<String, FavoritePointOfInterest>) {
        val list = map.toList()
            .sortedWith { o1, o2 -> o2.second.dateFavorite!!.compareTo(o1.second.dateFavorite) }
        radapter.submitList(list)
        radapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        val recycler: RecyclerView = findViewById(R.id.favorite_pois_recycler_view)
        radapter = FavoritePoisRecyclerAdapter()
        recycler.apply {
            layoutManager = LinearLayoutManager(this@FavoritePoisActivity)
            addItemDecoration(TopSpacingItemDecoration(CARD_PADDING))
            adapter = radapter
        }
    }
}