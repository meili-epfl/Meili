package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.User


object Auth : ViewModel() {
    private const val RC_SIGN_IN = 9001
    var name: String? = null
    var email: String? = null
    val isLoggedIn = MutableLiveData(false)

    var authService: AuthenticationService = FirebaseAuthenticationService()

    fun setAuthenticationService(authService: AuthenticationService) {
        this.authService = authService

        updateUserData()
    }

    fun getCurrentUser(): User? {
        return authService.getCurrentUser()
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
        authService.onActivityResult(activity, requestCode, result, data) {
            updateUserData()
        }
    }

    fun updateUserData() {
        val user = getCurrentUser()

        if (user != null) {
            name = user.username
            email = user.email
        } else {
            name = null
            email = null
        }

        isLoggedIn.value = (user != null)
    }
}