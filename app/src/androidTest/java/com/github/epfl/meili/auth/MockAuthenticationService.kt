package com.github.epfl.meili.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.models.User

class MockAuthenticationService : AuthenticationService {
    // These are vars since we modify these values during testing
    private var mockName = "Meili User"
    private var mockEmail = "meili.user@epfl.ch"
    private var mockId = "1234"


    override fun getCurrentUser(): User? {
        return if (mockName == "null") {
            null
        } else {
            User(mockId, mockName, mockEmail)
        }
    }

    override fun signInIntent(activity: AppCompatActivity?): Intent {
        val intent = Intent()
        intent.putExtra("MOCK_SINGIN", 123)
        return intent
    }

    override fun signOut() {
        mockName = "null"
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?, onComplete: () -> Unit) {
        onComplete()
    }
}