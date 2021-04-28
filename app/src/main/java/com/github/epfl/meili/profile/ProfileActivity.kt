package com.github.epfl.meili.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.tool.FirestoreTool
import com.github.epfl.meili.tool.StorageTool
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class ProfileActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

            val nimaPhoto =  findViewById<CircleImageView>(R.id.profilePhoto)
            nimaPhoto.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }

            val nimaName = findViewById<TextView>(R.id.tvName)
            val nimaDes = findViewById<TextView>(R.id.tvDescription)
            findViewById<Button>(R.id.btnModifyProfile).setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageTool.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreTool.updateCurrentUser(nimaName.text.toString(),
                            nimaDes.text.toString(), imagePath)
                    }
                else
                    FirestoreTool.updateCurrentUser(nimaName.text.toString(),
                        nimaDes.text.toString(), null)
            }
        }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            val nimaPhoto = findViewById<CircleImageView>(R.id.profilePhoto) as ImageView
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()
            nimaPhoto.setImageBitmap(selectedImageBmp)
            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreTool.getCurrentUser { user ->
            findViewById<EditText>(R.id.tvName).setText(user.username)
            findViewById<EditText>(R.id.tvDescription).setText(user.bio)
            val nimaPhoto = findViewById<CircleImageView>(R.id.profilePhoto) as ImageView
                if (!pictureJustChanged && user.profilePicturePath != null)
                    Picasso.get().load(Uri.parse(TODO())).into(nimaPhoto)
            }
        }
}