package com.github.epfl.meili.util

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage

/**
 * Uses FirebaseVision to detect landmarks in an image
 */
object LandmarkDetectionService {
    var firebaseVision: () -> FirebaseVision = { FirebaseVision.getInstance() }
    var firebaseVisionImage: (context: Context, imageUri: Uri) -> FirebaseVisionImage
        = { context, imageUri -> FirebaseVisionImage.fromFilePath(context, imageUri) }

    /**
     * Returns the FirebaseVision landmark detection task given an image uri and the application context
     */
    fun detectInImage(context: Context, imageUri: Uri): Task<List<FirebaseVisionCloudLandmark>> =
        firebaseVision().visionCloudLandmarkDetector.detectInImage(firebaseVisionImage(context, imageUri))
}