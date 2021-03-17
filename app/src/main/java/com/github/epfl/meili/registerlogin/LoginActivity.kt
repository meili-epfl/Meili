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
            loginUser()
        }

        findViewById<TextView>(R.id.back_to_registration_text_view).setOnClickListener {
            //Launch the register activity
            finish()
        }
    }

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.email_edittext_login).text.toString()
        val password = findViewById<EditText>(R.id.password_edittext_login).text.toString()

        if (!RegisterActivity.isSanitizedInput(this, email, password)) {
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //User logged in
                Log.d("LoginActivity", "SignInWithEmailAndPassword:success")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "SignInWithEmailAndPassword:failure ${it.message}")
                Toast.makeText(this, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}