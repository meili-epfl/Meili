package com.github.epfl.meili.util

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseUploadService {

    private val DEFAULT_DATABASE = { FirebaseStorage.getInstance() }

    var databaseProvider: () -> FirebaseStorage = DEFAULT_DATABASE

    private fun uploadFile(firebasePath: String, filePath: Uri, onSuccessCallback: () -> Unit, onFailureCallback: () -> Unit) {
        val ref = databaseProvider().getReference(firebasePath)
        ref.putFile(filePath)
            .addOnSuccessListener { _ -> onSuccessCallback() }
            .addOnFailureListener { _ -> onFailureCallback() }
    }

    fun uploadImage(name: String, filePath: Uri, onSuccessCallback: () -> Unit = {}, onFailureCallback: () -> Unit = {}) {
        uploadFile("images/$name", filePath, onSuccessCallback, onFailureCallback)
    }
}