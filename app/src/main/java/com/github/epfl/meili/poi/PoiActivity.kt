package com.github.epfl.meili.poi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.MenuActivity

class PoiActivity : MenuActivity(R.menu.nav_chat_menu) {
    //TODO: set parent of this to map and set this as parent of chat, review, forum
    //TODO: decide on good way to display the menus on top (since there are too many

    companion object {
        private const val NUM_PAGES = 3
        private const val INDEX_INFO = 0
        private const val INDEX_FORUM = 1
    }

    // View to swipe between info, forum and chat
    private lateinit var viewPager: ViewPager2
    private lateinit var poi: PointOfInterest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi)

        poi = intent.getParcelableExtra(MapActivity.POI_KEY)!!
        title = poi.name

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = PoiPagerAdapter(this)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }


    override fun onBackPressed() {
        if (viewPager.currentItem == INDEX_INFO) {
            // return to map
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class PoiPagerAdapter(aca: AppCompatActivity) : FragmentStateAdapter(aca) {
        override fun getItemCount(): Int = Companion.NUM_PAGES

        override fun createFragment(position: Int): Fragment =
            when (position) {
                INDEX_INFO -> PoiInfoFragment(poi)
                INDEX_FORUM -> PoiFragment(R.layout.forum_activity_placeholder)
                else -> PoiFragment(R.layout.chat_activity_placeholder)
            }
    }

}