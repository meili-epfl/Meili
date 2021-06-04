package com.github.epfl.meili.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.github.epfl.meili.models.User


object Auth : ViewModel(), AuthenticationService {
    private const val RC_SIGN_IN = 9001
    var name: String? = null
    var email: String? = null
    val isLoggedIn = MutableLiveData(false)

    var authService: AuthenticationService = FirebaseAuthenticationService()

    /**
     * Sets the auth service
     */
    fun setAuthenticationService(authService: AuthenticationService) {
        this.authService = authService

        updateUserData()
    }

    fun getCorrectAuthenticationService(): AuthenticationService {
        return if (AccessToken.getCurrentAccessToken() != null) {
            FacebookAuthenticationService()
        } else
            FirebaseAuthenticationService()
    }

    override fun getCurrentUser(): User? {
        return authService.getCurrentUser()
    }

    override fun signInIntent(activity: AppCompatActivity?): Intent {
        val signInIntent = authService.signInIntent(activity)

        activity!!.startActivityForResult(signInIntent, RC_SIGN_IN)
        return signInIntent
    }

    override fun signOut() {
        authService.signOut()

        updateUserData()
    }


    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        result: Int,
        data: Intent?,
        onComplete: () -> Unit
    ) {
        authService.onActivityResult(activity, requestCode, result, data) {
            updateUserData()
        }
    }

    private fun updateUserData() {
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