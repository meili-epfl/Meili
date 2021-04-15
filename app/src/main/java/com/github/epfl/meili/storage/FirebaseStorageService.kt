package com.github.epfl.meili.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageService: StorageService {

    private val firebaseStorage = FirebaseStorage.getInstance()

    override fun uploadFile(remotePath: String, filePath: Uri, onSuccessCallback: () -> Unit, onFailureCallback: () -> Unit) {
        firebaseStorage.getReference(remotePath).putFile(filePath)
                .addOnSuccessListener { _ -> onSuccessCallback() }
                .addOnFailureListener { _ -> onFailureCallback() }
    }

    override fun getDownloadUrl(remotePath: String, onSuccessListener: (Uri) -> Unit, onFailureListener: (Exception) -> Unit) {
        firebaseStorage.getReference(remotePath).downloadUrl
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
    }
}