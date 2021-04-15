package com.github.epfl.meili.photo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R


class PhotoDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_display)
        val imageView = findViewById<ImageView>(R.id.image_display)

        val uri = intent.getParcelableExtra<Uri>(CameraActivity.URI_KEY)
        imageView.setImageURI(uri)
    }
}