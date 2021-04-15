package com.github.epfl.meili.storage

import android.net.Uri

/**
 * Generic interface for uploading/downloading files to/from a remote database
 */
interface StorageService {
    /**
     * Upload a file from local storage to the remote database
     * remotePath (String): the path to be used in the remote database
     *                      eg.
     *                          Forum: "images/forum/{post_id}"
     *                          Profile: "images/avatar/{user_id}" or "images/profile/{user_id}"
     * filePath (Uri): the local path
     */
    fun uploadFile(
        remotePath: String,
        filePath: Uri,
        onSuccessCallback: () -> Unit={},
        onFailureCallback: () -> Unit={}
    )

    /**
     * Get the download url for a file in a remote database
     * remotePath (String): the path to identify the file in the remote database
     * onSuccessListener (Uri) -> Unit: the listener to handle the received download url
     */
    fun getDownloadUrl(
        remotePath: String,
        onSuccessListener: (Uri) -> Unit,
        onFailureListener: (Exception) -> Unit={}
    )
}