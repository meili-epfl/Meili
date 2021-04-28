package com.github.epfl.meili.photo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoCropBinding
import com.github.epfl.meili.photo.CameraActivity.Companion.URI_KEY
import com.github.epfl.meili.util.RotationGestureDetector

class PhotoCropActivity : AppCompatActivity(), RotationGestureDetector.OnRotationGestureListener {
    private lateinit var binding: ActivityPhotoCropBinding
    private lateinit var uri: Uri
    private lateinit var rotationGestureDetector: RotationGestureDetector

    private val launchPhotoEditActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.data != null && result.resultCode == RESULT_OK && result.data!!.data != null) {
                val intent = Intent()
                intent.data = result.data!!.data!!
                setResult(RESULT_OK, intent)
                finish()
            }
        }

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
        rotationGestureDetector = RotationGestureDetector(this, binding.photoEditImageView)
        binding.photoEditImageView.setOnTouchListener { _, event ->
            rotationGestureDetector.onTouchEvent(event)
        }
        binding.rotate90.setOnClickListener { onRotation(90f) }

        // Handle going to effects activity
        binding.effects.setOnClickListener { launchEffects() }

    }

    private fun startCrop() {
        binding.photoEditImageView.visibility = View.GONE
        binding.effects.visibility = View.GONE
        binding.crop.visibility = View.VISIBLE
        binding.cropImageContainer.visibility = View.VISIBLE
        binding.cropModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        binding.cropImageView.setImageBitmap(getRotatedBitmap())
    }

    private fun stopCrop() {
        binding.photoEditImageView.visibility = View.VISIBLE
        binding.crop.visibility = View.GONE
        binding.effects.visibility = View.VISIBLE
        binding.cropImageContainer.visibility = View.GONE
        binding.cropModeButton.setBackgroundColor(0)
    }

    private fun toggleCrop() {
        if (!binding.crop.isVisible) {
            startCrop()
        } else {
            stopCrop()
        }
    }

    private fun cropImage() {
        // Async callback to function defined in onCreate()
        binding.cropImageView.saveCroppedImageAsync(uri)
    }

    override fun onRotation(angle: Float) {
        binding.photoEditImageView.rotation += angle
    }

    private fun getRotatedBitmap(): Bitmap {
        val original = binding.photoEditImageView.drawToBitmap()
        val matrix = Matrix().apply { postRotate(binding.photoEditImageView.rotation) }
        binding.photoEditImageView.rotation = 0f
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }

    private fun launchEffects() {
        val intent = Intent(applicationContext, PhotoEditActivity::class.java)
        intent.putExtra(URI_KEY, uri)
        intent.setFlags(intent.getFlags() or Intent.FLAG_ACTIVITY_NO_HISTORY)
        launchPhotoEditActivity.launch(intent)
    }
}