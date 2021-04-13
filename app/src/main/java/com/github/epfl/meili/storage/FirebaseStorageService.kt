package com.github.epfl.meili.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageService: StorageService {

    private val firebaseStorage = FirebaseStorage.getInstance()

    override fun uploadFile(firebasePath: String, filePath: Uri, onSuccessCallback: () -> Unit, onFailureCallback: () -> Unit) {
        firebaseStorage.getReference(firebasePath).putFile(filePath)
                .addOnSuccessListener { _ -> onSuccessCallback() }
                .addOnFailureListener { _ -> onFailureCallback() }
    }

    override fun getDownloadUrl(firebasePath: String, onSuccessListener: (Uri) -> Unit, onFailureListener: (Exception) -> Unit) {
        firebaseStorage.getReference(firebasePath).downloadUrl
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
    }
}