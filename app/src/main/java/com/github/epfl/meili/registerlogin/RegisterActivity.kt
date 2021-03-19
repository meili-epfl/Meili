package com.github.epfl.meili.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.github.epfl.meili.LatestMessagesActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        CustomAuthentication.setAuthenticationService(CustomFirebaseAuthenticationService())

        findViewById<Button>(R.id.register_button).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_edittext_register).text.toString()
            val password = findViewById<EditText>(R.id.password_edittext_register).text.toString()
            val username = findViewById<EditText>(R.id.username_edittext_register).text.toString()

            CustomAuthentication.registerUser(this, email, password, username)
        }

        findViewById<TextView>(R.id.already_have_account_text_view).setOnClickListener {
            //Launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

    }


}