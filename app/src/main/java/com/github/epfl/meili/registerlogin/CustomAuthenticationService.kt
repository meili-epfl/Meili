package com.github.epfl.meili.registerlogin

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.LatestMessagesActivity
import com.github.epfl.meili.home.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface CustomAuthenticationService {

    fun init()

    fun createUser(activity: AppCompatActivity, email : String, password : String, username: String)


    fun signInWithEmailAndPassword(activity: AppCompatActivity, email: String, password: String)

}
