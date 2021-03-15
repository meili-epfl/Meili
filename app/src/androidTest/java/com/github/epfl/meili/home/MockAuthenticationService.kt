package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent

class MockAuthenticationService : AuthenticationService {
    var mock_name = "Meili User"
    var mock_email = "meili.user@epfl.ch"

    override fun init() {
        // Do nothing
    }

    override fun getCurrentuser(): AuthUser? {
        if (mock_name == "null") {
            return null
        } else {
            return AuthUser(mock_name, mock_email)
        }
    }

    override fun signInIntent(): Intent {
        val intent = Intent()
        intent.putExtra("MOCK_SINGIN", 123)
        return intent
    }

    override fun signOut() {
        // Do nothing
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?, onComplete: () -> Unit) {
        onComplete()
    }
}