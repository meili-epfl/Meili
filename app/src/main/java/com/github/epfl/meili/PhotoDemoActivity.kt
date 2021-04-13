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
import com.github.epfl.meili.R
import com.github.epfl.meili.storage.FirebaseStorageService
import com.github.epfl.meili.storage.StorageService
import com.squareup.picasso.Picasso

class PhotoDemoActivity : AppCompatActivity() {

    companion object {
        var storageService: () -> StorageService = { FirebaseStorageService }
    }

    private lateinit var choose: Button
    private lateinit var upload: Button
    private lateinit var show: Button
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
        setContentView(R.layout.activity_photo_demo)

        choose = findViewById(R.id.choose_button)
        upload = findViewById(R.id.upload_button)
        show = findViewById(R.id.show_button)
        imageView = findViewById(R.id.image_display)
    }

    private fun getDownloadUrlCallback(uri: Uri) {
        Picasso.get().load(uri).into(imageView)
    }

    fun onClick(view: View) {
        when(view) {
            choose -> getContent.launch("image/*")
            upload -> {
                if (filePath == null) {
                    Toast.makeText(applicationContext, "Choose an image first", Toast.LENGTH_SHORT).show()
                } else {
                    storageService().uploadFile("images/myfavimage", filePath!!, {
                        Toast.makeText(applicationContext, "Image successfully uploaded", Toast.LENGTH_SHORT).show()
                    })
                }
            }
            show -> {
                storageService().getDownloadUrl("images/myfavimage", { uri -> getDownloadUrlCallback(uri)})
            }
        }
    }
 }