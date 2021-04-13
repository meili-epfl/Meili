package com.github.epfl.meili.photo

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.theartofdev.edmodo.cropper.CropImageView


class PhotoDisplayActivity : AppCompatActivity() {
    private lateinit var uri: Uri

    private lateinit var imageView: ImageView
    private lateinit var cropImageView: CropImageView
    private lateinit var cropModeButton: ImageButton
    private lateinit var cancelButton: Button
    private lateinit var cropButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_display)

        // Initialize views
        imageView = findViewById<ImageView>(R.id.image_display)
        cropImageView = findViewById<CropImageView>(R.id.crop_image)
        cropImageView.setOnCropImageCompleteListener { view, result ->
            imageView.setImageDrawable(null) // required hack to update image using same uri
            imageView.setImageURI(uri) // Set imageView to new cropped image
            resetVisibilities() // Go back to main screen
        }

        cropModeButton = findViewById<ImageButton>(R.id.image_crop_mode_button)
        cancelButton = findViewById<Button>(R.id.image_cancel_button)
        cropButton = findViewById<Button>(R.id.image_crop_button)

        // Setup callbacks
        cropModeButton.setOnClickListener { cropMode() }
        cancelButton.setOnClickListener { resetVisibilities() }
        cropButton.setOnClickListener { cropImage() }

        resetVisibilities()

        // Set imageView to given image from camera activity
        uri = intent.getParcelableExtra<Uri>(CameraActivity.URI_KEY)!!
        imageView.setImageURI(uri)
    }

    /** Callback function for cancel button */
    private fun resetVisibilities() {
        // Set visibility of required views
        imageView.visibility = View.VISIBLE
        cropImageView.visibility = View.GONE
        cropModeButton.visibility = View.VISIBLE
        cancelButton.visibility = View.GONE
        cropButton.visibility = View.GONE
    }

    /** Callback function for crop mode button */
    private fun cropMode() {
        // Set visibility of required views
        imageView.visibility = View.GONE
        cropImageView.visibility = View.VISIBLE
        cropModeButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE
        cropButton.visibility = View.VISIBLE

        cropImageView.setImageUriAsync(uri) // Show image in crop tool
    }

    /** Callback function for crop button */
    private fun cropImage() {
        // Async callback to function defined in onCreate()
        cropImageView.saveCroppedImageAsync(uri)
    }
}