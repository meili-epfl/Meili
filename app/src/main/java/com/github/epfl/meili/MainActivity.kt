package com.github.epfl.meili

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.epfl.meili.home.GoogleSignInActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickLaunchSignInView(view: View){
        val intent = Intent(this, GoogleSignInActivity::class.java)
        startActivity(intent)
    }
}