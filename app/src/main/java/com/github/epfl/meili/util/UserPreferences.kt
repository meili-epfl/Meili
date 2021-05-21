package com.github.epfl.meili.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.PreferenceManager


class UserPreferences(private val context: AppCompatActivity) {
    companion object {
        private const val DARK_STATUS = "com.github.epfl.meili.DARK_STATUS"
        private const val FIRST_TIME = "com.github.epfl.meili.FIRST_USE"

        private val UI_MODES = listOf(MODE_NIGHT_FOLLOW_SYSTEM, MODE_NIGHT_NO, MODE_NIGHT_YES)
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

    var firstUse = preferences.getBoolean(FIRST_TIME, true)
        set(value) = preferences.edit().putBoolean(FIRST_TIME, value).apply()

    /** Applies the correct theme to the given context */
    fun applyMode(modeIndex: Int = darkMode) {
        setDefaultNightMode(UI_MODES[modeIndex])
        context.delegate.applyDayNight()
    }
}
