package com.github.epfl.meili.database

import android.net.Uri

/**
 * Generic interface for uploading/downloading files to/from a remote database
 */
interface StorageService {
    /**
     * Upload a ByteArray to the remote database
     * remotePath (String): the path to be used in the remote database
     *                      eg.
     *                          Forum: "images/forum/{post_id}"
     *                          Profile: "images/avatar/{user_id}" or "images/profile/{user_id}"
     */
    fun uploadBytes(
        remotePath: String,
        byteArray: ByteArray,
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