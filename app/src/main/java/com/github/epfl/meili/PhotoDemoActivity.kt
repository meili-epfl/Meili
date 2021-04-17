package com.github.epfl.meili

import android.content.ContentResolver
import android.graphics.Bitmap
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
import com.github.epfl.meili.storage.FirebaseStorageService
import com.github.epfl.meili.storage.StorageService
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoDemoActivity : AppCompatActivity() {

    companion object {
        private const val COMPRESSION_QUALITY = 75 // 0 (max compression) to 100 (min compression)

        var storageService: () -> StorageService = { FirebaseStorageService }
    }

    private lateinit var executor: ExecutorService

    private lateinit var choose: Button
    private lateinit var upload: Button
    private lateinit var show: Button
    private lateinit var imageView: ImageView

    private var bitmap: Bitmap? = null

    private fun compressed(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, stream)
        return stream.toByteArray()
    }

    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { filePath -> loadImage(filePath, contentResolver) }

    private fun loadImage(filePath: Uri, contentResolver: ContentResolver) {
        executor.execute {
            val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                getBitmap(contentResolver, filePath) // deprecated for SDK_INT >= 28
            } else {
                decodeBitmap(createSource(contentResolver, filePath))
            }

            runOnUiThread {
                this.bitmap = bitmap
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun compressAndUpload() {
        if (BuildConfig.DEBUG && bitmap == null) {
            error("Assertion failed")
        }

        executor.execute {
            storageService().uploadBytes("images/myfavimage", compressed(bitmap!!), {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Image successfully uploaded", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_demo)

        executor = Executors.newSingleThreadExecutor()

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
                if (bitmap == null) {
                    Toast.makeText(applicationContext, "Choose an image first", Toast.LENGTH_SHORT).show()
                } else {
                    compressAndUpload()
                }
            }
            show -> {
                storageService().getDownloadUrl("images/myfavimage", { uri -> getDownloadUrlCallback(uri)})
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
 }