package com.github.epfl.meili.util

import android.content.Context
import androidx.preference.PreferenceManager


class UserPreferences(context: Context?) {
    companion object {
        private const val DARK_STATUS = "io.github.meili.DARK_STATUS"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

}
