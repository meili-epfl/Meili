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

class PoiActivity : MenuActivity(R.menu.nav_poi_info_menu) {
    companion object {
        private const val NUM_PAGES = 1
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

    private inner class PoiPagerAdapter(aca: AppCompatActivity) : FragmentStateAdapter(aca) {
        override fun getItemCount(): Int = Companion.NUM_PAGES

        override fun createFragment(position: Int): Fragment =  PoiInfoFragment(poi)
    }

}