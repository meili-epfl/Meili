package com.github.epfl.meili.profile

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.util.ImageUtility
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator

class ProfileViewModel: ViewModel() {
    private val mUser: MutableLiveData<User> = MutableLiveData()
    private val mRequestCreator: MutableLiveData<RequestCreator> = MutableLiveData()
    private var bitmap: Bitmap? = null

    private lateinit var uid: String

    fun setUid(uid: String) {
        this.uid = uid
    }

    init {
        FirestoreDocumentService.getDocument("users/$uid").addOnSuccessListener {
            mUser.value = it.toObject(User::class.java)
            FirebaseStorageService.getDownloadUrl(
                    "avatars/$uid",
                    { uri -> loadImageIntoRequestCreator(uri)},
                    { /* do nothing in case of failure */ }
            )
        }
    }

    fun getUser(): LiveData<User> = mUser
    fun getRequestCreator(): LiveData<RequestCreator> = mRequestCreator

    fun updateProfile(user: User) {
        mUser.value = user
        FirestoreDocumentService.setDocument("users/$uid", user)
        if (bitmap != null) {
            ImageUtility.compressAndUploadToFirebase("avatars/$uid", bitmap!!)
        }
    }

    private fun loadImageIntoRequestCreator(uri: Uri) {
        mRequestCreator.value = Picasso.get().load(uri)
    }

    fun loadLocalImage(contentResolver: ContentResolver, uri: Uri) {
        loadImageIntoRequestCreator(uri)
        bitmap = ImageUtility.getBitmapFromFilePath(contentResolver, uri)
    }
}