package com.github.epfl.meili.util

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage

object LandmarkDetectionService {
    fun detectInImage(context: Context, imageUri: Uri): Task<List<FirebaseVisionCloudLandmark>> =
        FirebaseVision.getInstance().visionCloudLandmarkDetector
            .detectInImage(FirebaseVisionImage.fromFilePath(context, imageUri))
}