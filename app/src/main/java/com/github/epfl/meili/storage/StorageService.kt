package com.github.epfl.meili.storage

import android.net.Uri

interface StorageService {
    fun uploadFile(
        firebasePath: String,
        filePath: Uri,
        onSuccessCallback: () -> Unit={},
        onFailureCallback: () -> Unit={}
    )

    fun getDownloadUrl(
        firebasePath: String,
        onSuccessListener: (Uri) -> Unit,
        onFailureListener: (Exception) -> Unit={}
    )
}