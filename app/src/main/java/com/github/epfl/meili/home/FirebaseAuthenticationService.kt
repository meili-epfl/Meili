package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthenticationService : AuthenticationService {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun init() {
        val context = MainApplication.applicationContext()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

    }

    override fun getCurrentuser(): AuthUser? {
        var user = auth.currentUser

        if (user == null) {
            return user
        }

        return AuthUser(user.displayName, user.email)
    }

    override fun signInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun signOut() {
        auth.signOut()

        googleSignInClient.signOut()
    }

    fun firebaseAuthWithGoogle(activity: Activity, idToken: String, onComplete: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        onComplete()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        // ...
                        onComplete()
                    }
                }
    }

    override fun onActivityResult(
            activity: Activity,
            requestCode: Int,
            result: Int,
            data: Intent?,
            onComplete: () -> Unit
    ) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(activity, account.idToken!!, onComplete)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}