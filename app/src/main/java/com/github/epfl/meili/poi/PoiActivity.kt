package com.github.epfl.meili.poi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity
import com.google.android.gms.maps.model.PointOfInterest

class PoiActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi)

        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)
        val name = poi?.name
        title = name

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = PoiPagerAdapter(this)

        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == INDEX_FORUM) {
            // return to map
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class PoiPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = Companion.NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return if (position == INDEX_FORUM)
                PoiFragment(R.layout.forum_activity_placeholder)
            else
                PoiFragment(R.layout.chat_activity_placeholder)

        }
    }

    companion object {
        private const val NUM_PAGES = 2
        private const val INDEX_FORUM = 0
    }
}