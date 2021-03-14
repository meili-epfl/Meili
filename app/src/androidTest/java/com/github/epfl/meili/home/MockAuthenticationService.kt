package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent

class MockAuthenticationService: AuthenticationService {
    var MOCK_NAME = "Meili User"
    var MOCK_EMAIL = "meili.user@epfl.ch"

    override fun init() {
        // Do nothing
    }

    override fun getCurrentuser(): AuthUser? {
        if(MOCK_NAME == "null"){
            return null
        }else{
            return AuthUser(MOCK_NAME, MOCK_EMAIL)
        }
    }

    override fun signInIntent(): Intent {
        val intent = Intent()
        intent.putExtra("MOCK_SINGIN", 123)
        return intent
    }

    override fun signOut() {
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?, onComplete: () -> Unit) {
        onComplete()
    }
}