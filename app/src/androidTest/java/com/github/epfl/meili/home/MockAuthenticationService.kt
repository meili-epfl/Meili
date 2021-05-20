package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import com.github.epfl.meili.models.User

class MockAuthenticationService : AuthenticationService {
    // These are vars since we modify these values during testing
    var mock_name = "Meili User"
    var mock_email = "meili.user@epfl.ch"
    var mock_id = "1234"


    override fun getCurrentUser(): User? {
        return if (mock_name == "null") {
            null
        } else {
            User(mock_id, mock_name, mock_email)
        }
    }

    override fun signInIntent(): Intent {
        val intent = Intent()
        intent.putExtra("MOCK_SINGIN", 123)
        return intent
    }

    override fun signOut() {
        mock_name = "null"
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?, onComplete: () -> Unit) {
        onComplete()
    }
}