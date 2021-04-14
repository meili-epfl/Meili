package com.github.epfl.meili.photo

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.theartofdev.edmodo.cropper.CropImageView
import tech.picnic.fingerpaintview.FingerPaintImageView


class PhotoEditActivity : AppCompatActivity() {
    private lateinit var uri: Uri

    private lateinit var imageEditView: FingerPaintImageView
    private lateinit var cropImageView: CropImageView
    private lateinit var cropModeButton: ImageButton
    private lateinit var cancelButton: Button
    private lateinit var cropButton: Button
    private lateinit var fabDone: FloatingActionButton

    private lateinit var previewContainer: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_edit)

        // Initialize views
        imageEditView = findViewById(R.id.image_edit_view)
        cropImageView = findViewById(R.id.crop_image)
        cropImageView.setOnCropImageCompleteListener { _, _ ->
            imageEditView.clear()
            imageEditView.setImageDrawable(null) // required hack to update image using same uri
            imageEditView.setImageURI(uri) // Set imageView to new cropped image
            resetVisibilities() // Go back to main screen
        }

        cropModeButton = findViewById(R.id.image_crop_mode_button)
        cancelButton = findViewById(R.id.image_cancel_button)
        cropButton = findViewById(R.id.image_crop_button)

        // Setup callbacks
        cropModeButton.setOnClickListener { cropMode() }
        cancelButton.setOnClickListener { resetVisibilities() }
        cropButton.setOnClickListener { cropImage() }


        previewContainer = findViewById(R.id.previewContainer)
        fabDone = findViewById(R.id.fabDone)
        fabDone.setOnClickListener {
            previewContainer.visibility = View.VISIBLE
            imageEditView.visibility = View.GONE
            cropModeButton.visibility = View.GONE
            fabDone.visibility = View.GONE

            findViewById<ImageView>(R.id.preview).setImageDrawable(imageEditView.drawable)


        }
        findViewById<TextView>(R.id.close).setOnClickListener {
            resetVisibilities()
        }
        resetVisibilities()

        // Set imageView to given image from camera activity
        uri = intent.getParcelableExtra(CameraActivity.URI_KEY)!!
        imageEditView.setImageURI(uri)
    }

    /** Callback function for cancel button */
    private fun resetVisibilities() {
        // Set visibility of required views
        imageEditView.visibility = View.VISIBLE
        cropImageView.visibility = View.GONE
        cropModeButton.visibility = View.VISIBLE
        cancelButton.visibility = View.GONE
        cropButton.visibility = View.GONE
        previewContainer.visibility = View.GONE
        fabDone.visibility = View.VISIBLE
    }

    /** Callback function for crop mode button */
    private fun cropMode() {
        // Set visibility of required views
        imageEditView.visibility = View.GONE
        cropImageView.visibility = View.VISIBLE
        cropModeButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE
        cropButton.visibility = View.VISIBLE
        fabDone.visibility = View.GONE

        //cropImageView.setImageUriAsync(uri) // Show image in crop tool
        cropImageView.setImageBitmap((imageEditView.drawable as BitmapDrawable).bitmap)
    }

    /** Callback function for crop button */
    private fun cropImage() {
        // Async callback to function defined in onCreate()
        cropImageView.saveCroppedImageAsync(uri)
    }
}