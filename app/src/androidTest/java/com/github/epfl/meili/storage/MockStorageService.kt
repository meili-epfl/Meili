package com.github.epfl.meili.storage

import android.net.Uri
import com.github.epfl.meili.storage.StorageService

object MockStorageService: StorageService {
    override fun uploadBytes(
        remotePath: String,
        byteArray: ByteArray,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        onSuccessCallback()
    }

    override fun getDownloadUrl(
        remotePath: String,
        onSuccessListener: (Uri) -> Unit,
        onFailureListener: (Exception) -> Unit
    ) {
        onSuccessListener(Uri.EMPTY)
    }
}