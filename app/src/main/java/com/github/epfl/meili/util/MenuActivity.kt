package com.github.epfl.meili.util

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class MenuActivity(private val menuId: Int): AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflaterHelper.onCreateOptionsMenuHelper(this, menuId, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MenuInflaterHelper.onOptionsItemSelectedHelper(this, item, intent)
        return super.onOptionsItemSelected(item)
    }
}