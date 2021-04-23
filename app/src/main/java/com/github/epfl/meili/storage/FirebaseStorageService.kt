package com.github.epfl.meili.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageService: StorageService {

    private val DEFAULT_STORAGE = { FirebaseStorage.getInstance() }
    var storageProvider: () -> FirebaseStorage = DEFAULT_STORAGE

    override fun uploadBytes(remotePath: String, byteArray: ByteArray, onSuccessCallback: () -> Unit, onFailureCallback: () -> Unit) {
        storageProvider().getReference(remotePath).putBytes(byteArray)
            .addOnSuccessListener { _ -> onSuccessCallback() }
            .addOnFailureListener { _ -> onFailureCallback() }
    }
    override fun getDownloadUrl(remotePath: String, onSuccessListener: (Uri) -> Unit, onFailureListener: (Exception) -> Unit) {
        storageProvider().getReference(remotePath).downloadUrl
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
    }
}