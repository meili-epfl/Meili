package com.github.epfl.meili.photo

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoEditorBinding
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter


class PhotoEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoEditorBinding
    private lateinit var uri: Uri
    private lateinit var photoEditor: PhotoEditor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uri = intent.getParcelableExtra(CameraActivity.URI_KEY)!!
        binding.photoEditorView.source.setImageURI(uri)

        photoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()
        stopDrawing()

        binding.show.setOnClickListener { showPreview() }
        binding.hide.setOnClickListener { hidePreview() }
        binding.paintModeButton.setOnClickListener { toggleDrawing() }
        binding.filters.setOnClickListener { toggleFilters() }
        binding.colorSlider.setOnColorChangeListener { _, _, _ -> changeDrawingColor() }

        binding.bw.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.NONE) }
        binding.sepia.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SEPIA) }
        binding.sharpen.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SHARPEN) }
        binding.fishEye.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.FISH_EYE) }
        binding.saturate.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SATURATE) }
        binding.undo.setOnClickListener { photoEditor.undo() }
        binding.redo.setOnClickListener { photoEditor.redo() }
    }

    /** Callback function for show preview button */
    private fun showPreview() {
        binding.previewContainer.visibility = View.VISIBLE
        binding.photoEditorView.visibility = View.GONE
        binding.show.visibility = View.GONE
        binding.preview.setImageURI(uri)
        binding.paintModeButton.visibility = View.GONE
        binding.undo.visibility = View.GONE
        binding.redo.visibility = View.GONE
        stopDrawing()
    }

    /** Callback function for hide preview button */
    private fun hidePreview() {
        binding.previewContainer.visibility = View.GONE
        binding.photoEditorView.visibility = View.VISIBLE
        binding.show.visibility = View.VISIBLE
        binding.paintModeButton.visibility = View.VISIBLE
        binding.undo.visibility = View.VISIBLE
        binding.redo.visibility = View.VISIBLE
        stopDrawing()
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

    /** Callback function for paint mode */
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

    /** Callback function for filters mode */
    private fun toggleFilters() {
        if (!binding.filtersContainer.isVisible)
            startFilters()
        else
            stopFilters()
    }


    private fun changeDrawingColor() {
        photoEditor.brushColor = binding.colorSlider.color
    }


}