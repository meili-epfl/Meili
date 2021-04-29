package com.github.epfl.meili.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.github.epfl.meili.database.FirebaseStorageService
import java.io.ByteArrayOutputStream

object ImageUtility {
    private const val COMPRESSION_QUALITY = 75 // 0 (max compression) to 100 (loss-less compression)

    fun compressAndUploadToFirebase(remotePath: String, bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, stream)
        FirebaseStorageService.uploadBytes(remotePath, stream.toByteArray())
    }

    fun getBitmapFromFilePath(contentResolver: ContentResolver, filePath: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, filePath) // deprecated for SDK_INT >= 28
        } else {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, filePath))
        }
    }
}