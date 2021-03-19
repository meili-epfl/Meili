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

class CustomFirebaseAuthenticationService: CustomAuthenticationService {
    private lateinit var auth: FirebaseAuth

    override fun init(){
        auth = Firebase.auth
    }

    override fun createUser(activity: AppCompatActivity, email : String, password : String, username: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //if it is successful
                Log.d("register_test", "createUserWithEmail:success")
                saveUserToFirebaseDatabase(activity, username)
            }
            .addOnFailureListener {
                Log.d("register_test", "createUserWithEmail:failure")
                Toast.makeText(activity, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(activity: AppCompatActivity, username: String) {

        // Write a message to the database


        // Create post document (ID created by database)
        val userDocument = hashMapOf(
            "uid" to auth.uid,
            "username" to username,
        )

        // Add a new document with a generated ID
        Firebase.firestore.collection("users").add(userDocument)
            .addOnSuccessListener {
                Log.d(RegisterActivity.TAG, "user saved to firebase datatbase!")
                val intent = Intent(activity, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent)
            }
            .addOnFailureListener { auth.currentUser?.delete() }
    }

    override fun signInWithEmailAndPassword(activity: AppCompatActivity, email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //User logged in
                Log.d("LoginActivity", "SignInWithEmailAndPassword:success")
                val intent = Intent(activity, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "SignInWithEmailAndPassword:failure ${it.message}")
                Toast.makeText(activity, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
