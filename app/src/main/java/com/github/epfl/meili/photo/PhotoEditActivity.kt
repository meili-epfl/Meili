package com.github.epfl.meili.photo

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoEditBinding


class PhotoEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoEditBinding
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cropImageView.setOnCropImageCompleteListener { _, _ ->
            binding.paintImageView.clear() // clear drawn lines so that they don't get duplicated after crop
            binding.paintImageView.setImageDrawable(null) // required hack to update image using same uri
            binding.paintImageView.setImageURI(uri) // Set imageView to new cropped image
            resetVisibilities() // Go back to main screen
        }

        // Setup callbacks
        binding.cropModeButton.setOnClickListener { cropMode() }
        binding.cancelButton.setOnClickListener { resetVisibilities() }
        binding.cropButton.setOnClickListener { cropImage() }
        binding.show.setOnClickListener { showPreview() }
        binding.hide.setOnClickListener { resetVisibilities() }
        binding.paintModeButton.setOnClickListener { togglePaint() }

        resetVisibilities()

        // Set imageView to given image from camera activity
        uri = intent.getParcelableExtra(CameraActivity.URI_KEY)!!
        binding.paintImageView.setImageURI(uri)
    }

    /** Callback function for preview mode */
    private fun showPreview() {
        binding.previewContainer.visibility = View.VISIBLE
        binding.paintImageView.visibility = View.GONE
        binding.cropModeButton.visibility = View.GONE
        binding.show.visibility = View.GONE
        binding.paintModeButton.visibility = View.GONE
        binding.paintImageView.inEditMode = false
        binding.paintModeButton.setBackgroundColor(0)

        findViewById<ImageView>(R.id.preview).setImageDrawable(binding.paintImageView.drawable)
    }

    /** Callback function for paint mode */
    private fun togglePaint() {
        binding.paintImageView.inEditMode = !binding.paintImageView.inEditMode
        if (binding.paintImageView.inEditMode)
            binding.paintModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        else
            binding.paintModeButton.setBackgroundColor(0)
    }

    /** Callback function for cancel button */
    private fun resetVisibilities() {
        // Set visibility of required views
        binding.paintImageView.visibility = View.VISIBLE
        binding.cropImageView.visibility = View.GONE
        binding.cropModeButton.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.GONE
        binding.cropButton.visibility = View.GONE
        binding.previewContainer.visibility = View.GONE
        binding.show.visibility = View.VISIBLE
        binding.paintModeButton.visibility = View.VISIBLE
        binding.paintImageView.inEditMode = false
        binding.paintModeButton.setBackgroundColor(0)
    }

    /** Callback function for crop mode button */
    private fun cropMode() {
        // Set visibility of required views
        binding.paintImageView.visibility = View.GONE
        binding.cropImageView.visibility = View.VISIBLE
        binding.cropModeButton.visibility = View.GONE
        binding.cancelButton.visibility = View.VISIBLE
        binding.cropButton.visibility = View.VISIBLE
        binding.show.visibility = View.GONE
        binding.paintModeButton.visibility = View.GONE
        binding.paintImageView.inEditMode = false
        binding.paintModeButton.setBackgroundColor(0)

        binding.cropImageView.setImageBitmap((binding.paintImageView.drawable as BitmapDrawable).bitmap) // Show image in crop tool
    }

    /** Callback function for crop button */
    private fun cropImage() {
        // Async callback to function defined in onCreate()
        binding.cropImageView.saveCroppedImageAsync(uri)
    }
}