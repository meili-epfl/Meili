package com.github.epfl.meili.storage

import android.net.Uri
import com.github.epfl.meili.storage.StorageService

object MockStorageService: StorageService {
    override fun uploadFile(
        firebasePath: String,
        filePath: Uri,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        onSuccessCallback()
    }

    override fun getDownloadUrl(
        firebasePath: String,
        onSuccessListener: (Uri) -> Unit,
        onFailureListener: (Exception) -> Unit
    ) {
        onSuccessListener(Uri.EMPTY)
    }
}