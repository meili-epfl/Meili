package com.github.epfl.meili

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.github.epfl.meili.ProfileActivity
import com.github.epfl.meili.tool.FirestoreUtil
import com.google.firebase.iid.FirebaseInstanceId
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class GoogleSignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        AuthenticationService.isLoggedIn.observe(this, {
            updateUI()
            //FirestoreUtil.initCurrentUserIfFirstTime {
            //}
        })
        /*val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .setRequireName(true)
                .build()))
            .build()
        startActivityForResult(intent, RC_SIGN_IN)

         */
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

        /*if (requestCode == RC_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(intentFor<MainActivity>().newTask().clearTask())

                    //val registrationToken = FirebaseInstanceId.getInstance().token
                    //MyFirebaseInstanceIDService.addTokenToFirestore(registrationToken)

                }
            }

        }

         */

        AuthenticationService.onActivityResult(this, requestCode, resultCode, data)
    }

}