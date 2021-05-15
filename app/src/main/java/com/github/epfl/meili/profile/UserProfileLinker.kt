package com.github.epfl.meili.profile

import android.app.Activity
import android.content.Intent

interface UserProfileLinker {
    fun openUserProfile(friendUid: String, activity: Activity) {
        val intent =
                Intent(activity.applicationContext, ProfileActivity::class.java).putExtra(ProfileActivity.USER_KEY, friendUid)
        activity.startActivity(intent)
    }
}