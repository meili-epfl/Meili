package com.github.epfl.meili.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager


class UserPreferences(context: AppCompatActivity?) {
    companion object {
        private const val DARK_STATUS = "io.github.meili.DARK_STATUS"
    }

    private val context = context
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

    /** Applies the correct theme to the given context */
    fun checkTheme(chosen: Int) {
        when (chosen) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        context!!.delegate.applyDayNight()
    }

}
