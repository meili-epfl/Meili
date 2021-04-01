package com.github.epfl.meili

import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.util.FirebaseUploadService

class PhotoTestActivity : AppCompatActivity() {

    private lateinit var choose: Button
    private lateinit var upload: Button
    private lateinit var imageView: ImageView
    private var filePath: Uri? = null

    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { filePath ->
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                getBitmap(contentResolver, filePath)
            } else {
                decodeBitmap(createSource(contentResolver, filePath))
            }

            this.filePath = filePath
            imageView.setImageBitmap(bitmap)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_test)

        choose = findViewById(R.id.choose_button)
        upload = findViewById(R.id.upload_button)
        imageView = findViewById(R.id.image_display)
    }

    fun onClick(view: View) {
        when(view) {
            choose -> getContent.launch("image/*")
            upload -> {
                if (filePath == null) {
                    Toast.makeText(applicationContext, "Choose an image first", Toast.LENGTH_SHORT).show()
                } else {
                    FirebaseUploadService.uploadImage("myfavimage", filePath!!, {
                        Toast.makeText(applicationContext, "Image successfully uploaded", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(applicationContext, "Image could not be uploaded", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }
 }