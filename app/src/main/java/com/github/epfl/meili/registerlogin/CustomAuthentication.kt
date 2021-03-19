package com.github.epfl.meili.registerlogin

import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.LatestMessagesActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object CustomAuthentication : ViewModel() {
    val TOAST_MESSAGE = "Please enter Email and Password"

    lateinit var authService: CustomAuthenticationService

    fun setAuthenticationService(authService: CustomAuthenticationService) {
        this.authService = authService

        authService.init()

    }

    fun registerUser(
        activity: AppCompatActivity,
        email: String,
        password: String,
        username: String
    ) {

        if (!isSanitizedInput(activity, email, password)) {
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(activity, "Please enter a username", Toast.LENGTH_SHORT).show()
            return
        }



        authService.createUser(activity, email, password, username)
    }



    fun loginUser(activity: AppCompatActivity, email: String, password: String) {


        if (!isSanitizedInput(activity, email, password)) {
            return;
        }

        authService.signInWithEmailAndPassword(activity, email, password)
    }


    private fun isSanitizedInput(
        activity: AppCompatActivity,
        email: String,
        password: String
    ): Boolean {
        return if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, TOAST_MESSAGE, Toast.LENGTH_SHORT).show()
            false;
        } else {
            true;
        }
    }
}