package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


object Auth : ViewModel() {
    private const val RC_SIGN_IN = 9001
    var name: String? = null
    var email: String? = null
    val isLoggedIn = MutableLiveData<Boolean>(false)

    lateinit var authService: AuthenticationService


    fun setAuthenticationService(authService: AuthenticationService) {
        this.authService = authService

        Auth.authService.init()

        updateUserData()
    }

    fun getCurrentUser(): AuthUser? {
        return authService.getCurrentuser()
    }

    fun signIn(activity: Activity) {
        val signInIntent = authService.signInIntent()

        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        authService.signOut()

        updateUserData()
    }


    fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?) {
        authService.onActivityResult(activity, requestCode, result, data) { updateUserData() }
    }

    fun updateUserData() {
        val user = getCurrentUser()

        if (user != null) {
            name = user.name
            email = user.email
        } else {
            name = null
            email = null
        }

        isLoggedIn.value = (user != null)
    }
}