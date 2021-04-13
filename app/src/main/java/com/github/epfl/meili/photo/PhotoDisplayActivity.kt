package com.github.epfl.meili.photo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.theartofdev.edmodo.cropper.CropImageView


class PhotoDisplayActivity : AppCompatActivity() {
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_display)

        // Setup callbacks
        val cropButton = findViewById<ImageButton>(R.id.image_crop_button)
        cropButton.setOnClickListener { cropMode() }

        val imageView = findViewById<ImageView>(R.id.image_display)
        uri = intent.getParcelableExtra<Uri>(CameraActivity.URI_KEY)!!
        imageView.setImageURI(uri)
    }

    /** Callback function for crop button */
    private fun cropMode() {
        val cropImageView = findViewById<CropImageView>(R.id.crop_image)
        cropImageView.setImageUriAsync(uri)
    }
}