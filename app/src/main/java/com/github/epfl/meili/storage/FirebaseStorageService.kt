package com.github.epfl.meili.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageService: StorageService {

    private val DEFAULT_DATABASE = { FirebaseStorage.getInstance() }

    var storageProvider: () -> FirebaseStorage = DEFAULT_DATABASE

    override fun uploadFile(firebasePath: String, filePath: Uri, onSuccessCallback: () -> Unit, onFailureCallback: () -> Unit) {
        storageProvider().getReference(firebasePath).putFile(filePath)
                .addOnSuccessListener { _ -> onSuccessCallback() }
                .addOnFailureListener { _ -> onFailureCallback() }
    }

    override fun getDownloadUrl(firebasePath: String, onSuccessListener: (Uri) -> Unit, onFailureListener: (Exception) -> Unit) {
        storageProvider().getReference(firebasePath).downloadUrl
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
    }
}