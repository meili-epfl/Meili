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

    fun OnMapViewButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
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
        var message: String = "Sign In to use the map"
        var buttonMessage = "Sign In"

        //disable map button if not signed in
        val button = findViewById<Button>(R.id.mapButton)
        button.isEnabled = false

        if (Auth.isLoggedIn.value!!) {
            message = "Welcome "+Auth.name!!+"!"
            buttonMessage = "Sign Out"
            button.isEnabled = true
        }

        findViewById<TextView>(R.id.textFieldSignIn).text = message
        findViewById<Button>(R.id.signInButton).text = buttonMessage
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Auth.onActivityResult(this, requestCode, resultCode, data)
    }
}