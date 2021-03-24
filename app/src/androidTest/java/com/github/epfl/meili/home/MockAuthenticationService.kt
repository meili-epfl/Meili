package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import com.github.epfl.meili.models.User

class MockAuthenticationService : AuthenticationService {

    companion object {
        const val mock_name = "Meili User"
        const val mock_email = "meili.user@epfl.ch"
        const val mock_id = "1234"
    }

    override fun getCurrentuser(): User? {
        if (mock_name == "null") {
            return null
        } else {
            return User(mock_id, mock_name, mock_email)
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