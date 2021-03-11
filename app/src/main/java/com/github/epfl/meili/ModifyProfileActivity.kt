package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ModifyProfileActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_profile)
    }

    fun sendUpdates(view: View) {
            // update the name here

        val newName = findViewById<EditText>(R.id.etName)
        val newPhone = findViewById<EditText>(R.id.etPhone).text.toString()
        val newDescription = findViewById<EditText>(R.id.etDescription).text.toString()

        val nameUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(newName.text.toString())
            .build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(nameUpdate)

        //FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(newPhone)

        val intent = Intent(this, ProfileActivity::class.java)
        val extras = Bundle()
        extras.putString("EXTRA_USERNAME", newName.text.toString())
        extras.putString("EXTRA_PHONE", newPhone)
        extras.putString("EXTRAS_DES", newDescription)
        intent.putExtras(extras)
        startActivity(intent)
    }

    fun cancelGoBack(){
        //bug when cancel if fields are filled
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}