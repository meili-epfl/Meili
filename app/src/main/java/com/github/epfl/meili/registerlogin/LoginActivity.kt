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
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        findViewById<Button>(R.id.login_button).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_edittext_login).text.toString()
            val password = findViewById<EditText>(R.id.password_edittext_login).text.toString()
            Log.d("login", "email is $email and password is $password")
            CustomAuthentication.loginUser(this, email, password)
        }

        findViewById<TextView>(R.id.back_to_registration_text_view).setOnClickListener {
            //Launch the register activity
            finish()
        }
    }


}