package com.github.epfl.meili.database

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageService: StorageService {

    var storageProvider: () -> FirebaseStorage = { FirebaseStorage.getInstance() }

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