package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.home.GoogleSignInActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.registerlogin.RegisterActivity
=======
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
>>>>>>> modified the use of fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        val intent: Intent = when (view.id) {
            R.id.launchSignInView -> {
                Intent(this, GoogleSignInActivity::class.java)
            }
            R.id.launchChatView -> {
                Intent(this, RegisterActivity::class.java)
            }
            R.id.launchMapView -> {
                Intent(this, MapActivity::class.java)
            }
            else -> {
                Intent(this, MainActivity::class.java)
            }
        }



    fun openProfile(view: View) {
        if (FirebaseAuth.getInstance().currentUser == null){
            val intent = Intent(this, GoogleSignInActivity::class.java)
            startActivity(intent)
        }else {

            val intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }
    }


        fun onClickLaunchSignInView(view: View) {
            val intent = Intent(this, GoogleSignInActivity::class.java)
            startActivity(intent)
        }
    }
}