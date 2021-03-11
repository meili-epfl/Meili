package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthenticationService: ViewModel() {
    private var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient
    private const val TAG = "GoogleActivity"
    private const val RC_SIGN_IN = 9001
    var name: String? = null
    var email: String? = null
    val isLoggedIn = MutableLiveData<Boolean>(false)


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

        updateUserData()

    }

    fun getCurrentUser(): FirebaseUser? {
       return auth.currentUser
    }

    fun signIn(activity: Activity) {

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut(){
        auth.signOut()

        googleSignInClient.signOut() //Add on complete listener

        updateUserData()
    }

    fun firebaseAuthWithGoogle(activity: Activity, idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        updateUserData()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        // ...
                        updateUserData()
                    }
                }
    }

    fun onActivityResult(activity: Activity, requestCode: Int, result: Int, data: Intent?){
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(activity, account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    fun updateUserData(){
        val user = getCurrentUser()

        if (user != null){
            name = user.displayName
            email = user.email
        }else{
            name = null
            email = null
        }

        isLoggedIn.value = (user != null)
    }
}