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
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoEditBinding
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.PhotoFilter
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


        if (!isPermissionGranted()) {
            getStoragePermission() // photo will get stored on phone once it is done being edited
        }
        setFabListener()

        binding.emojis.setOnClickListener { showEmojiTable() }
        makeEmojiTable()
    }

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


    private fun startDrawing() {
        stopFilters()
        photoEditor.setBrushDrawingMode(true)
        binding.paintModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        binding.colorSlider.visibility = View.VISIBLE
    }

    private fun stopDrawing() {
        photoEditor.setBrushDrawingMode(false)
        binding.paintModeButton.setBackgroundColor(0)
        binding.colorSlider.visibility = View.GONE
    }

    private fun toggleDrawing() {
        if (!photoEditor.brushDrawableMode)
            startDrawing()
        else
            stopDrawing()
    }

    private fun startFilters() {
        stopDrawing()
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

    private fun showEmojiTable() {
        binding.emojiContainer.visibility = View.VISIBLE
    }


    private fun changeDrawingColor() {
        photoEditor.brushColor = binding.colorSlider.color
    }

    private fun makeEmojiTable() {
        val emojis = PhotoEditor.getEmojis(this)
        var curRow = TableRow(this)

        for (i in 0 until emojis.size) {
            // Make new row every 6 emojis
            if (i % 6 == 0 && i != 0) {
                binding.emojiTable.addView(curRow)
                curRow = TableRow(this)
            }

            // Make textView
            val textView = TextView(this)
            textView.textSize = 50f
            textView.text = emojis[i]
            textView.setOnClickListener { addEmoji(emojis[i]) }

            // Add emoji to current row
            curRow.addView(textView)
        }
    }

    private fun addEmoji(emoji: String) {
        binding.emojiContainer.visibility = View.GONE
        photoEditor.addEmoji(emoji)
    }

    companion object {
        private const val REQUEST_CODE: Int = 1
    }
}