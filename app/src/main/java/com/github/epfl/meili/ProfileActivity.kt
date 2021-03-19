package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View

import android.app.Activity
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
//import com.github.epfl.meili.GlideApp
import com.bumptech.glide.Glide.with
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_profile.*

import com.google.firebase.auth.FirebaseAuth
import com.github.epfl.meili.tool.FirestoreUtil
import com.github.epfl.meili.tool.StorageUtil
import kotlinx.android.synthetic.main.activity_modify_profile.*
import kotlinx.android.synthetic.main.activity_profile.profilePhoto
import java.io.ByteArrayOutputStream


class ProfileActivity : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)

        view.apply {
            val nimaPhoto =  findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profilePhoto)
            nimaPhoto.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }

            val nimaModButton = findViewById<Button>(R.id.btnModifyProfile)
            nimaModButton.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(tvName.text.toString(),
                            tvDescription.text.toString(), imagePath)
                    }
                else
                    FirestoreUtil.updateCurrentUser(tvName.text.toString(),
                        tvDescription.text.toString(), null)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()
            Glide.with(this)
                .load(selectedImageBytes)
                .into(profilePhoto)

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this@ProfileActivity.isVisible) {
                val nimaName = view?.findViewById<EditText>(R.id.etName)
                nimaName?.setText(user.name)
                val nimaDes = view?.findViewById<EditText>(R.id.etDescription)
                nimaDes?.setText(user.bio)
                if (!pictureJustChanged && user.profilePicturePath != null)
                    Glide.with(this)
                        .load(StorageUtil.pathToReference(user.profilePicturePath))
                        //.placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(profilePhoto)
            }
        }
    }

}