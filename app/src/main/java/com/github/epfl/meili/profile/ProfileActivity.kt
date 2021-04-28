package com.github.epfl.meili.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import de.hdodenhof.circleimageview.CircleImageView


class ProfileActivity : AppCompatActivity() {

    private val launchGallery =  registerForActivityResult(ActivityResultContracts.GetContent())
                                    { viewModel.loadLocalImage(contentResolver, it) }

    private lateinit var photoView: CircleImageView
    private lateinit var nameView: EditText
    private lateinit var bioView: EditText
    private lateinit var saveButton: Button

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (BuildConfig.DEBUG && !(Auth.isLoggedIn.value!!)) {
            error("Assertion failed")
        }

        photoView = findViewById(R.id.photo)
        nameView = findViewById(R.id.name)
        bioView = findViewById(R.id.bio)
        saveButton = findViewById(R.id.save)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.setUid(Auth.getCurrentUser()!!.uid)
        viewModel.getUser().observe(this) { user ->
            nameView.setText(user.username)
            bioView.setText(user.bio)
        }

        viewModel.getRequestCreator().observe(this) { it.into(photoView) }
    }

    fun onClick(view: View) {
        when (view) {
            photoView -> launchGallery.launch("image/*")
            saveButton -> saveProfile()
        }
    }

    private fun saveProfile() {
        val user = Auth.getCurrentUser()!!
        user.username = nameView.text.toString()
        user.bio = bioView.text.toString()
        viewModel.updateProfile(user)
    }
}