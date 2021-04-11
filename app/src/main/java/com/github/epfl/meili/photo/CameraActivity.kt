package com.github.epfl.meili.photo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.github.epfl.meili.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {
    private val TAG = "CameraActivity"

    private var imageCapture: ImageCapture? = null // is null when camera hasn't started
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var outputDirectory: File
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        startCameraIfPermitted()

        // Setup button's callback function
        val cameraButton = findViewById<ImageButton>(R.id.camera_capture_button)
        cameraButton.setOnClickListener { takePhoto() }

        val cameraSwitch = findViewById<ImageButton>(R.id.camera_switch_button)
        cameraSwitch.setOnClickListener { switchCamera() }

        val previewView = findViewById<PreviewView>(R.id.camera_preview)

        // Listen to pinch gestures
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Get the camera's current zoom ratio
                val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0F

                // Update the camera's zoom ratio. This is an asynchronous operation that returns
                // a ListenableFuture, allowing you to listen to when the operation completes.
                camera.cameraControl.setZoomRatio(currentZoomRatio * detector.scaleFactor)

                // Return true, as the event was handled
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(applicationContext, listener)

        // Listen to tap events on the viewfinder and set them as focus regions
        previewView.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            view.performClick()
            scaleGestureDetector.onTouchEvent(motionEvent)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    // Get the MeteringPointFactory from PreviewView
                    val factory = previewView.meteringPointFactory

                    // Create a MeteringPoint from the tap coordinates
                    val point = factory.createPoint(motionEvent.x, motionEvent.y)

                    // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
                    val action = FocusMeteringAction.Builder(point).build()

                    // Trigger the focus and metering. The method returns a ListenableFuture since the operation
                    // is asynchronous. You can use it get notified when the focus is successful or if it fails.
                    camera.cameraControl.startFocusAndMetering(action)

                    true
                }
                else -> false
            }
        }


        // Set up extra camera features
        makePhotosHaveOrientation()

        // Determine the output directory
        outputDirectory = getOutputDirectory()
    }

    /** Sets up camera */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get() // Guaranteed to exist

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                //hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT // not working yet
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Setup Preview use case --> to display the preview to the screen
            preview = Preview.Builder().build()

            // Setup Image Capture use case --> allows user to take photos
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Setup which camera to select (default is back)
            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            // Display preview in activity
            val previewView = findViewById<PreviewView>(R.id.camera_preview)
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Attach the lifecycle of the camera to the activity's lifecycle
            camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    /** Takes a picture */
    private fun takePhoto() {
        if (imageCapture == null) {
            Log.d(TAG, "Camera is not set up correctly")
            return
        }

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up behaviour for when a photo is taken
        imageCapture!!.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object :
                ImageCapture.OnImageSavedCallback { // object with callback functions for pictures
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val intent = Intent(applicationContext, PhotoDisplayActivity::class.java)
                    intent.putExtra(URI_KEY, savedUri)
                    startActivity(intent)
                }
            })
    }

    /** Checks if device has access to start the camera, if not, ask the user for permission */
    private fun startCameraIfPermitted() {
        if (allPermissionsGranted()) {
            startCamera() // When authorized, start the camera
        } else {
            // Ask device for permission to use the camera
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun switchCamera() {
//        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
//            CameraSelector.LENS_FACING_BACK
//        } else {
//            CameraSelector.LENS_FACING_FRONT
//        }
        lensFacing = CameraSelector.LENS_FACING_FRONT
        //TODO: make it work
    }


    /** Checks if the app's access to the camera is granted */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Callback function for permissions */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Checks if the request code is the same as the one sent to the device
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera() // When authorized, start the camera
            } else {
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> { // Take photo when volume down is pressed
                val shutter = findViewById<ImageButton>(R.id.camera_capture_button)
                shutter.apply {
                    performClick()
                    isPressed = true
                    invalidate()
                    postDelayed({ // Press for a small delay to show that button has been pressed
                        invalidate()
                        isPressed = false
                    }, 200L)
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    /** Sets up rotation metadata for Image Capture use case */
    private fun makePhotosHaveOrientation() {
        if (imageCapture == null) {
            Log.d(TAG, "Camera is not set up correctly")
            return
        }

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture!!.targetRotation = rotation
            }
        }
        orientationEventListener.enable()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = applicationContext.externalMediaDirs.firstOrNull()?.let {
            File(it, baseContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else applicationContext.filesDir
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    /** Returns true if the device has an available front camera. False otherwise */
//    private fun hasFrontCamera(): Boolean {
//        return cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
//    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val URI_KEY = "URI_KEY"
    }
}