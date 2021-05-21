package com.github.epfl.meili.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.PreferenceManager


class UserPreferences(private val context: AppCompatActivity) {
    companion object {
        private const val DARK_STATUS = "com.github.epfl.meili.DARK_STATUS"
        private const val FIRST_USE = "com.github.epfl.meili.FIRST_USE"

        private val UI_MODES = listOf(MODE_NIGHT_FOLLOW_SYSTEM, MODE_NIGHT_NO, MODE_NIGHT_YES)
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

    var firstUse = preferences.getBoolean(FIRST_USE, true)
        set(value) = preferences.edit().putBoolean(FIRST_USE, value).apply()

    /** Applies the correct theme to the given context */
    fun applyTheme() {
        setDefaultNightMode(UI_MODES[darkMode])
        context.delegate.applyDayNight()
    }
}
