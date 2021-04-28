package com.github.epfl.meili.profile

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.User
import com.github.epfl.meili.photo.PhotoService
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
        FirestoreDocument.getDocument("users/$uid").addOnSuccessListener {
            mUser.value = it.toObject(User::class.java)
            mUser.
        }
    }

    fun getUser(): LiveData<User> = mUser
    fun getRequestCreator(): LiveData<RequestCreator> = mRequestCreator

    fun updateProfile() {
        TODO("update firesotre doc")
        TODO("save photo to firebase storage")
    }

    fun loadImage(filePath: Uri) {
        mRequestCreator.value = Picasso.get().load(filePath)
    }
}