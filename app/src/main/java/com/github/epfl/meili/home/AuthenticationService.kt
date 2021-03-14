package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent


interface AuthenticationService {
    fun init()

    fun getCurrentuser(): AuthUser?

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

class AuthUser(val name: String, val email: String)