package com.github.epfl.meili.photo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoEditBinding
import com.github.epfl.meili.util.RotationGestureDetector
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


class PhotoEditActivity : AppCompatActivity(), RotationGestureDetector.OnRotationGestureListener {

    private lateinit var binding: ActivityPhotoEditBinding
    private lateinit var uri: Uri
    private val rotationGestureDetector = RotationGestureDetector(this)


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*binding.paintImageView.setOnTouchListener { _, event ->
            rotationGestureDetector.onTouchEvent(event) // Make crop container listen to touch events
        }*/

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
        binding.paintModeButton.setOnClickListener { toggleDrawing() }
        binding.colorSlider.setOnColorChangeListener { _, _, _ -> changeDrawingColor() }

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
        stopDrawing()

        findViewById<ImageView>(R.id.preview).setImageDrawable(binding.paintImageView.drawable)
    }

    private fun changeDrawingColor() {
        binding.paintImageView.strokeColor = binding.colorSlider.color
    }

    private fun startDrawing() {
        binding.paintImageView.inEditMode = true
        binding.paintModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        binding.colorSlider.visibility = View.VISIBLE
    }

    private fun stopDrawing() {
        binding.paintImageView.inEditMode = false
        binding.paintModeButton.setBackgroundColor(0)
        binding.colorSlider.visibility = View.GONE
    }


    /** Callback function for paint mode */
    private fun toggleDrawing() {
        if (!binding.paintImageView.inEditMode)
            startDrawing()
        else
            stopDrawing()

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
        stopDrawing()
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
        stopDrawing()

        binding.cropImageView.setImageBitmap((binding.paintImageView.drawable as BitmapDrawable).bitmap) // Show image in crop tool
    }

    /** Callback function for crop button */
    private fun cropImage() {
        // Async callback to function defined in onCreate()
        binding.cropImageView.saveCroppedImageAsync(uri)
    }

    /** Callback function for RotationGestureDetector.OnRotationGestureListener
     * Rotates crop image when two finger rotation motion */
    override fun onRotation(angle: Float) {
        val bitmap = (binding.paintImageView.drawable as BitmapDrawable).bitmap // Get bitmap

        val radians = Math.toRadians(angle.toDouble())
        val sin = abs(sin(radians))
        val cos = abs(cos(radians))
        // figure out total width and height of new bitmap
        val width = bitmap.width
        val height = bitmap.height
        val newWidth = (width * cos + height * sin).toInt()
        val newHeight = (width * sin + height * cos).toInt()

        val matrix =
            Matrix().apply { postRotate(angle, width / 2f, height / 2f) } // rotation matrix
                .apply {
                    postTranslate(
                        (newWidth - width) / 2f,
                        (newHeight - height) / 2f
                    )
                } // Translation matrix

        // Apply rotation
        val rotated = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            width,
            height,
            matrix,
            true
        )

        binding.paintImageView.setImageBitmap(rotated) // Show image
    }
}