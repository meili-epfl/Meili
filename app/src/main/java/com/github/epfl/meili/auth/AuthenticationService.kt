package com.github.epfl.meili.auth

import android.app.Activity
import android.content.Intent
import com.github.epfl.meili.models.User


interface AuthenticationService {
    /**
     * Returns the current user
     */
    fun getCurrentUser(): User?

    /**
     * Returns the sign in intent
     */
    fun signInIntent(activity: Activity?): Intent

    /**
     * Signs the user out
     */
    fun signOut()

    /**
     * Called when the activity exits
     */
    fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        result: Int,
        data: Intent?,
        onComplete: () -> Unit
    )
}
