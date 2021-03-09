package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val extras = intent.extras
        if(extras!=null) {
            val username_string = extras!!.getString("EXTRA_USERNAME")
            val phone_string = extras!!.getString("EXTRA_PHONE")
            val des_string = extras!!.getString("EXTRAS_DES")

            val tvName = findViewById<TextView>(R.id.tvName).apply {
                text = username_string
            }

            val tvPhone = findViewById<TextView>(R.id.tvPhone).apply {
                text = phone_string
            }
            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            tvDescription.setText(des_string)
        }
    }

    fun modProfile(view: View) {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        startActivity(intent)
    }

}