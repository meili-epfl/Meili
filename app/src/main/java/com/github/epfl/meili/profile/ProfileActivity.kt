package com.github.epfl.meili.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.R
import com.github.epfl.meili.models.User
import com.github.epfl.meili.photo.PhotoService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ProfileActivity : AppCompatActivity() {

    private val launchGallery =  registerForActivityResult(ActivityResultContracts.GetContent()) { viewModel.loadImage(it) }

    private lateinit var photoView: CircleImageView
    private lateinit var nameView: EditText
    private lateinit var bioView: EditText
    private lateinit var saveButton: Button

    private lateinit var executor: ExecutorService

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        executor = Executors.newSingleThreadExecutor()

        photoView = findViewById(R.id.photo)
        nameView = findViewById(R.id.name)
        bioView = findViewById(R.id.bio)
        saveButton = findViewById(R.id.save)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
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

    }
}