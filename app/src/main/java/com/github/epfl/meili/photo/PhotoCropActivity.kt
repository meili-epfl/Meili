package com.github.epfl.meili.photo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoCropBinding
import com.github.epfl.meili.photo.CameraActivity.Companion.URI_KEY
import com.github.epfl.meili.util.RotationGestureDetector
import java.io.FileOutputStream
import java.io.IOException

/** This activity handles rotations and cropping of the image, after having used the camera, and
 * before applying any effects */
class PhotoCropActivity : AppCompatActivity(), RotationGestureDetector.OnRotationGestureListener {
    companion object {
        private const val COMPRESSION_QUALITY = 100 // 0 (max compression) to 100 (loss-less compression)
    }

    private lateinit var binding: ActivityPhotoCropBinding
    private lateinit var uri: Uri
    private lateinit var rotationGestureDetector: RotationGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // display image which was received from camera
        uri = intent.getParcelableExtra(URI_KEY)!!
        binding.photoEditImageView.setImageURI(uri)

        // Handle cropping
        binding.cropModeButton.setOnClickListener { toggleCrop() }
        binding.crop.setOnClickListener { cropImage() }
        binding.cropImageView.setOnCropImageCompleteListener { _, _ ->
            binding.photoEditImageView.setImageDrawable(null) // required hack to update image using same uri
            binding.photoEditImageView.setImageURI(uri) // Set imageView to new cropped image
            stopCrop() // Go back to main screen
        }

        // Handle rotations
        rotationGestureDetector = RotationGestureDetector(this)
        binding.photoEditImageView.setOnTouchListener { _, event ->
            rotationGestureDetector.onTouchEvent(event)
        }
        binding.rotate90.setOnClickListener { onRotation(90f) }

        // Handle going to effects activity
        binding.effects.setOnClickListener { launchEffects() }

        // Notify user of the two finger rotation feature
        Toast.makeText(applicationContext, "You can rotate using two fingers !", Toast.LENGTH_SHORT).show()
    }

    /** Handles which view is visible and sets up the cropping tool */
    private fun startCrop() {
        // UI
        binding.photoEditImageView.visibility = View.GONE
        binding.effects.visibility = View.GONE
        binding.crop.visibility = View.VISIBLE
        binding.cropImageContainer.visibility = View.VISIBLE
        binding.cropModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))

        // Setup of cropper
        binding.cropImageView.setImageBitmap(getRotatedBitmap())
    }

    /** Handles which view is visible */
    private fun stopCrop() {
        binding.photoEditImageView.visibility = View.VISIBLE
        binding.crop.visibility = View.GONE
        binding.effects.visibility = View.VISIBLE
        binding.cropImageContainer.visibility = View.GONE
        binding.cropModeButton.setBackgroundColor(0)
        binding.photoEditImageView.rotation = 0f // Reset rotation
    }

    /** Toggle between cropping modes */
    private fun toggleCrop() {
        if (!binding.crop.isVisible) {
            startCrop()
        } else {
            stopCrop()
        }
    }

    /** Callback function for the crop button, saves the cropped image */
    private fun cropImage() {
        // Async callback to function defined in onCreate()
        binding.cropImageView.saveCroppedImageAsync(uri)
    }

    /** Callback function for RotationGestureListener, updates angle of imageView */
    override fun onRotation(angle: Float) {
        binding.photoEditImageView.rotation += angle
    }

    /** Get a rotated bitmap from the ImageView rotation */
    private fun getRotatedBitmap(): Bitmap {
        val original = binding.photoEditImageView.drawToBitmap()
        val matrix = Matrix().apply {
            postRotate(binding.photoEditImageView.rotation) // rotation matrix
        }
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }

    /** Launch PhotoEditActivity */
    private fun launchEffects() {
        // Save rotated image into uri
        try {
            FileOutputStream(uri.path).use { out ->
                getRotatedBitmap().compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Launch new activity
        val intent = Intent(this, PhotoEditActivity::class.java)
        intent.putExtra(URI_KEY, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT) // This specifies that the ActivityResult from PhotoEditActivity has to be forwarded to CameraActivity
        startActivity(intent)
        finish()
    }
}