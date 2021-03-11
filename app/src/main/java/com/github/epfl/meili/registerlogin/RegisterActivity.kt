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
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        findViewById<Button>(R.id.register_button).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_edittext_register).text.toString()
            val password = findViewById<EditText>(R.id.password_edittext_register).text.toString()
            registerUser(email, password)
        }

        findViewById<TextView>(R.id.already_have_account_text_view).setOnClickListener {
            //Launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

    }

    private fun registerUser(email: String, password: String) {

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                //if it is successful
                Log.d("register_test", "createUserWithEmail:success")
                saveUserToFirebaseDatabase()
            }
            .addOnFailureListener{
                Log.d("register_test", "createUserWithEmail:failure")
                Toast.makeText(this, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase() {

        // Write a message to the database
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val database = Firebase.database
        val myRef = database.getReference("/users/$uid")
        val user = User(uid, findViewById<EditText>(R.id.username_edittext_register).text.toString())
        if(user.username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().currentUser?.delete()

        }else{
            myRef.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "user saved to firebase datatbase!")
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
        }


    }


}