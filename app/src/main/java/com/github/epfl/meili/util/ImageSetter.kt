package com.github.epfl.meili.util

import android.widget.ImageView
import com.github.epfl.meili.database.FirebaseStorageService
import com.squareup.picasso.Picasso

object ImageSetter {
    val imageAvatarPath: (String) -> String = { uid -> "images/avatars/${uid}" }
    val imagePostPath: (String) -> String = { postId -> "images/forum/${postId}" }

    fun setImageInto(id: String, image: ImageView, pathBuilder: (String) -> String) {
        FirebaseStorageService.getDownloadUrl(
            pathBuilder(id),
            { uri -> Picasso.get().load(uri).into(image) },
            { /* do nothing in case of failure */ }
        )
    }
}