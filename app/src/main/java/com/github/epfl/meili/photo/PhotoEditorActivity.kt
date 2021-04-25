package com.github.epfl.meili.photo

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import com.github.epfl.meili.R
import com.github.epfl.meili.databinding.ActivityPhotoEditorBinding
import com.github.epfl.meili.util.RotationGestureDetector
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter


class PhotoEditorActivity : AppCompatActivity(), RotationGestureDetector.OnRotationGestureListener {
    private lateinit var binding: ActivityPhotoEditorBinding
    private lateinit var uri: Uri
    private lateinit var photoEditor: PhotoEditor
    private lateinit var rotationGestureDetector: RotationGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uri = intent.getParcelableExtra(CameraActivity.URI_KEY)!!
        binding.photoEditorView.source.setImageURI(uri)

        photoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()

        binding.show.setOnClickListener { showPreview() }
        binding.hide.setOnClickListener { hidePreview() }
        binding.paintModeButton.setOnClickListener { toggleDrawing() }
        binding.filters.setOnClickListener { toggleFilters() }
        binding.cropModeButton.setOnClickListener { toggleCrop() }
        binding.colorSlider.setOnColorChangeListener { _, _, _ -> changeDrawingColor() }
        binding.emojis.setOnClickListener { toggleEmojis() }

        binding.bw.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.NONE) }
        binding.sepia.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SEPIA) }
        binding.sharpen.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SHARPEN) }
        binding.fishEye.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.FISH_EYE) }
        binding.saturate.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SATURATE) }
        binding.undo.setOnClickListener { photoEditor.undo() }
        binding.redo.setOnClickListener { photoEditor.redo() }
        binding.crop.setOnClickListener { cropImage() }

        // Setup callback for when an image is cropped
        binding.cropImageView.setOnCropImageCompleteListener { _, _ ->
            binding.photoEditorView.source.setImageDrawable(null) // required hack to update image using same uri
            binding.photoEditorView.source.setImageURI(uri) // Set imageView to new cropped image
            stopCrop() // Go back to main screen
        }

        // Make paintImageView listen to rotation events
        rotationGestureDetector = RotationGestureDetector(this, binding.photoEditorView.source)
        binding.photoEditorView.source.setOnTouchListener { _, event ->
            rotationGestureDetector.onTouchEvent(event)
        }

        makeEmojiTable()
        stopDrawing()
    }

    private fun showPreview() {
        binding.previewContainer.visibility = View.VISIBLE
        binding.photoEditorView.visibility = View.GONE
        binding.show.visibility = View.GONE
        binding.preview.setImageBitmap(getRotatedBitmap())
        binding.paintModeButton.visibility = View.GONE
        binding.cropModeButton.visibility = View.GONE
        binding.undo.visibility = View.GONE
        binding.redo.visibility = View.GONE
        stopDrawing()
    }

    private fun hidePreview() {
        binding.previewContainer.visibility = View.GONE
        binding.photoEditorView.visibility = View.VISIBLE
        binding.show.visibility = View.VISIBLE
        binding.paintModeButton.visibility = View.VISIBLE
        binding.cropModeButton.visibility = View.VISIBLE
        binding.undo.visibility = View.VISIBLE
        binding.redo.visibility = View.VISIBLE
        stopDrawing()
    }

    private fun startDrawing() {
        stopFilters()
        stopCrop()
        rotationGestureDetector.rotatable = false
        photoEditor.setBrushDrawingMode(true)
        binding.paintModeButton.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        binding.colorSlider.visibility = View.VISIBLE
    }

    private fun stopDrawing() {
        photoEditor.setBrushDrawingMode(false)
        rotationGestureDetector.rotatable = true
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
        stopCrop()
        rotationGestureDetector.rotatable = false
        binding.filters.setBackgroundColor(getColor(R.color.quantum_bluegrey100))
        binding.filtersContainer.visibility = View.VISIBLE
    }

    private fun stopFilters() {
        rotationGestureDetector.rotatable = true
        binding.filters.setBackgroundColor(0)
        binding.filtersContainer.visibility = View.GONE
    }

    private fun toggleFilters() {
        if (!binding.filtersContainer.isVisible)
            startFilters()
        else
            stopFilters()
    }

    private fun startCrop() {
        stopDrawing()
        stopFilters()
        binding.cropContainer.visibility = View.VISIBLE

        binding.cropImageView.setImageBitmap(getRotatedBitmap())
    }

    private fun stopCrop() {
        binding.cropContainer.visibility = View.GONE
        binding.photoEditorView.source.rotation = 0f
    }

    private fun toggleCrop() {
        if (!binding.cropContainer.isVisible) {
            startCrop()
        } else {
            stopCrop()
        }
    }

    private fun toggleEmojis() {
        binding.emojiContainer.visibility = View.VISIBLE
    }

    private fun changeDrawingColor() {
        photoEditor.brushColor = binding.colorSlider.color
    }

    /** Callback function for crop button */
    private fun cropImage() {
        // Async callback to function defined in onCreate()
        binding.cropImageView.saveCroppedImageAsync(uri)
    }

    /** Callback function for RotationGestureDetector.OnRotationGestureListener
     * Rotates crop image when two finger rotation motion */
    override fun onRotation(angle: Float) {
        binding.photoEditorView.source.rotation += angle
    }

    private fun getRotatedBitmap(): Bitmap {
        val original = binding.photoEditorView.source.drawToBitmap()
        val matrix = Matrix().apply { postRotate(binding.photoEditorView.source.rotation) }
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
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
}