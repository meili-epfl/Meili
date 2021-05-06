package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthenticationService : AuthenticationService {
    private var auth: FirebaseAuth
    private val googleSignInClient: GoogleSignInClient

    init {
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

    fun setAuth(authService: FirebaseAuth) {
        auth = authService
    }

    override fun getCurrentUser(): User? {
        val user: FirebaseUser? = auth.currentUser

        return if (user == null) {
            null
        } else {
            return User(user.uid, user.displayName!!, user.email!!, " ")
        }
    }

    override fun signInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun signOut() {
        auth.signOut()

        googleSignInClient.signOut()
    }

    private fun firebaseAuthWithGoogle(activity: Activity, idToken: String, onComplete: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(activity) { onComplete() }
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?, onComplete: () -> Unit) {
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