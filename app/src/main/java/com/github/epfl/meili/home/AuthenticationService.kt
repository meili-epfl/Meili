package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import com.github.epfl.meili.models.User


interface AuthenticationService {
    fun getCurrentuser(): User?

    fun signInIntent(): Intent

    fun signOut()

    fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        result: Int,
        data: Intent?,
        onComplete: () -> Unit
    )
}
