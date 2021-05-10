package com.github.epfl.meili.util

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


object UIUtility {
    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            if (null == activity.currentFocus) null else activity.currentFocus!!
                .windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}