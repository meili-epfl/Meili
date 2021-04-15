package com.github.epfl.meili.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity

class GoogleSignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        Auth.setAuthenticationService(FirebaseAuthenticationService())

        Auth.isLoggedIn.observe(this, {
            updateUI()
        })

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI()
    }

    fun onGoogleButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        if (Auth.getCurrentUser() != null) {
            signOut()
        } else {
            signIn()
        }
    }

    private fun signIn() {
        Auth.signIn(this)
    }

    private fun signOut() {
        Auth.signOut()
    }

    private fun updateUI() {
        var message: String = "Sign in"
        var buttonMessage = "Sign In"

        if (Auth.isLoggedIn.value!!) {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textFieldSignIn).text = message
        findViewById<Button>(R.id.signInButton).text = buttonMessage
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Auth.onActivityResult(this, requestCode, resultCode, data)
    }
}