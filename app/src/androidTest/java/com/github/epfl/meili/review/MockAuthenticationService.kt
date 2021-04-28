package com.github.epfl.meili.review

import android.app.Activity
import android.content.Intent
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User

class MockAuthenticationService : AuthenticationService {

    private var mockUid: String = "1234"
    private var signedIn: Boolean = false

    fun setMockUid(uid: String) {
        mockUid = uid
    }

    override fun getCurrentUser(): User? {
        return if (signedIn) {
            User(mockUid, "", "", " ", null)
        } else {
            null
        }
    }

    override fun signInIntent(): Intent {
        signedIn = true
        Auth.isLoggedIn.postValue(true)
        return Intent()
    }

    override fun signOut() {
        signedIn = false
        Auth.isLoggedIn.postValue(false)
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?, onComplete: () -> Unit) {
        onComplete()
    }
}