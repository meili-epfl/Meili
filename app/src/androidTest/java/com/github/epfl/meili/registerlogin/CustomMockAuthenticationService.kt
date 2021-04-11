package com.github.epfl.meili.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.LatestMessagesActivity

class CustomMockAuthenticationService : CustomAuthenticationService{

    override fun init() {
        //Do nothing
    }

    override fun createUser(
        activity: AppCompatActivity,
        email: String,
        password: String,
        username: String
    ) {
        val intent = Intent(activity, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    override fun signInWithEmailAndPassword(
        activity: AppCompatActivity,
        email: String,
        password: String
    ) {
        val intent = Intent(activity, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }
}