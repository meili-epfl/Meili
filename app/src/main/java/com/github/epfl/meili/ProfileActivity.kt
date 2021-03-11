package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.home.GoogleSignInActivity
import com.google.firebase.auth.FirebaseAuth


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //verifyUserIsLoggedIn()


        val tvName = findViewById<TextView>(R.id.tvName)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvEmail = findViewById<TextView>(R.id.tvMail)


        FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
            // if the user is logged in, display their info on the screen
            tvEmail.setText(firebaseUser.email)
            tvName.setText(firebaseUser.displayName)
            tvPhone.setText(firebaseUser.phoneNumber)
            //Picasso.get().load(firebaseUser.photoUrl).into(ivUserImage)
        }

        val extras = intent.extras
        if(extras!=null) {
            val username_string = extras!!.getString("EXTRA_USERNAME")
            val phone_string = extras!!.getString("EXTRA_PHONE")
            val des_string = extras!!.getString("EXTRAS_DES")

            if(username_string!=null) {
                tvName.apply {
                    text = username_string
                }
            }

            if(phone_string!=null) {
                tvPhone.apply {
                    text = phone_string
                }
            }

            if(des_string!=null) {
                val tvDescription = findViewById<TextView>(R.id.tvDescription)
                tvDescription.setText(des_string)
            }
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, GoogleSignInActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    fun modProfile(view: View) {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        startActivity(intent)
    }

}