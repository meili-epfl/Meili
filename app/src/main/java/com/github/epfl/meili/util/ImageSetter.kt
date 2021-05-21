package com.github.epfl.meili.util

import com.github.epfl.meili.database.FirebaseStorageService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

object ImageSetter {
    var imageAvatarPath: (String) -> String = { uid -> "images/avatars/${uid}" }

    fun setImageInto(uid: String, image: CircleImageView) {
        FirebaseStorageService.getDownloadUrl(
                imageAvatarPath(uid),
                { uri -> Picasso.get().load(uri).into(image) },
                { /* do nothing in case of failure */ }
        )
    }
}