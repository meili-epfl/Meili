package com.github.epfl.meili.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.util.UserPreferences

class GoogleSignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        Auth.setAuthenticationService(FirebaseAuthenticationService())

        Auth.isLoggedIn.observe(this, {
            updateUI()
        })
    }

    /**
     * Launches the map activity, called when the user doesn't want to sign in and so has reduced functionality
     */
    fun onMapViewButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        goToMap()
    }

    /**
     * Callback for signin button
     */
    fun onGoogleButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        if (Auth.getCurrentUser() != null) {
            signOut()
        } else {
            signIn()
        }
    }

    private fun goToMap() {
        val preferences = UserPreferences(this)
        preferences.firstTime = false
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }

    private fun signIn() {
        Auth.signInIntent(this)
    }

    private fun signOut() {
        Auth.signOut()
    }

    private fun updateUI() {
        var message = ""
        var buttonMessage = getString(R.string.sign_in_button_message)

        if (Auth.isLoggedIn.value!!) {
            message = String.format(getString(R.string.welcome_message), Auth.name!!)
            buttonMessage = getString(R.string.sign_out_button_message)
            goToMap()
        }

        findViewById<TextView>(R.id.textFieldSignIn).text = message
        findViewById<Button>(R.id.signInButton).text = buttonMessage
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Auth.onActivityResult(this, requestCode, resultCode, data) {}
    }
}