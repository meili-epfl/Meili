package com.github.epfl.meili.photo

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoEditBinding
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.ViewType
import java.io.File


/**
 * An activity which is launched after a photo has been taken by the camera activity. It lets the user edit the photo by drawing, adding filters, etc.
 */
class PhotoEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoEditBinding
    private lateinit var uri: Uri
    private lateinit var photoEditor: PhotoEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // display image which was received from camera
        uri = intent.getParcelableExtra(CameraActivity.URI_KEY)!!
        binding.photoEditorView.source.setImageURI(uri)

        photoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()
        stopDrawing() // default

        binding.undo.setOnClickListener { photoEditor.undo() }
        binding.redo.setOnClickListener { photoEditor.redo() }

        // drawing on photo
        binding.paintModeButton.setOnClickListener { toggleDrawing() }
        binding.colorSlider.setOnColorChangeListener { _, _, _ -> changeDrawingColor() }


        binding.filters.setOnClickListener { toggleFilters() }
        // filter buttons
        binding.bw.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.NONE) }
        binding.sepia.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SEPIA) }
        binding.sharpen.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SHARPEN) }
        binding.fishEye.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.FISH_EYE) }
        binding.saturate.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SATURATE) }
        // text
        binding.tvText.setOnClickListener { toggleText() }
        //enable text editing
        photoEditor.setOnPhotoEditorListener( object: OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) { photoEditor.editText(rootView, binding.etText.text.toString(), binding.colorSlider.color); }
            override fun onAddViewListener(p0: ViewType?, p1: Int) {} override fun onRemoveViewListener(p0: ViewType?, p1: Int) {}
            override fun onStartViewChangeListener(p0: ViewType?) {} override fun onStopViewChangeListener(p0: ViewType?) {} })

        // photo will get stored on phone once it is done being edited
        if (!isPermissionGranted()) { getStoragePermission() }
        setFabListener() }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionGranted()) {
            setFabListener()
        }
    }

    /*
     * When fab is clicked, send edited photo back to camera, which will then send it back to activity which launched camera
     */
    @SuppressLint("MissingPermission")
    private fun setFabListener() {
        binding.fab.setOnClickListener {
            uri.path?.let { it1 ->
                photoEditor.saveAsFile(it1, object : OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        val intent = Intent()
                        intent.data = Uri.fromFile(File(imagePath))
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(exception: Exception) {
                        Log.e("PhotoEditor", "Failed to save Image")
                    }
                })
            }
        }
    }

    private fun getStoragePermission() {
        if (isPermissionGranted() && BuildConfig.DEBUG) {
            error("Failed assertion")
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            WRITE_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED
    }

    private fun toggleText(){
        if(!binding.etText.isVisible){
            stopFilters()
            stopDrawing()
            binding.etText.visibility = View.VISIBLE
            binding.buttonAddText.visibility= View.VISIBLE
            binding.etText.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
            binding.tvText.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
            binding.colorSlider.visibility = View.VISIBLE

            binding.buttonAddText.setOnClickListener {
                photoEditor.addText(binding.etText.text.toString(), binding.colorSlider.color)
            }
        }
        else{
            stopText()
        }
    }

    private fun stopText(){
        binding.colorSlider.visibility = View.GONE
        binding.etText.visibility = View.GONE
        binding.buttonAddText.visibility= View.GONE
        binding.tvText.setBackgroundColor(0)
    }

    private fun stopDrawing() {
        photoEditor.setBrushDrawingMode(false)
        binding.paintModeButton.setBackgroundColor(0)
        binding.colorSlider.visibility = View.GONE
    }

    private fun toggleDrawing() {
        //start drawing, function combined because only 20 functions allowed by code climate
        if (!photoEditor.brushDrawableMode) {
            stopText()
            stopFilters()
            photoEditor.setBrushDrawingMode(true)
            binding.paintModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
            binding.colorSlider.visibility = View.VISIBLE
        }
        else
            stopDrawing()
    }

    private fun startFilters() {
        stopDrawing()
        stopText()
        binding.filters.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        binding.filtersContainer.visibility = View.VISIBLE
    }

    private fun stopFilters() {
        binding.filters.setBackgroundColor(0)
        binding.filtersContainer.visibility = View.GONE
    }

    private fun toggleFilters() {
        if (!binding.filtersContainer.isVisible)
            startFilters()
        else
            stopFilters()
    }



    private fun changeDrawingColor() {
        photoEditor.brushColor = binding.colorSlider.color
    }

    companion object {
        private const val REQUEST_CODE: Int = 1
    }
}