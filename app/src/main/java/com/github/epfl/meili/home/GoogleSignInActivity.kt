package com.github.epfl.meili.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R

class GoogleSignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        AuthenticationService.isLoggedIn.observe(this, {
            updateUI()
        })

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = AuthenticationService.getCurrentUser()
        updateUI()
    }

    fun onGoogleButtonClick(view: View) {
        if (AuthenticationService.getCurrentUser() != null) {
            signOut()
        } else {
            signIn()
        }
    }

    private fun signIn() {
        AuthenticationService.signIn(this)
    }

    private fun signOut() {
        AuthenticationService.signOut()
    }

    private fun updateUI() {
        var message: String = "Sign in"
        var buttonMessage = "Sign In"
        if (AuthenticationService.isLoggedIn.value!!) {
            message = AuthenticationService.name!!
            buttonMessage = "Sign Out"
        }

        val textView = findViewById<TextView>(R.id.textFieldSignIn).apply {
            text = message
        }

        val buttonView = findViewById<Button>(R.id.signInButton).apply {
            text = buttonMessage
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        AuthenticationService.onActivityResult(this, requestCode, resultCode, data)
    }

}