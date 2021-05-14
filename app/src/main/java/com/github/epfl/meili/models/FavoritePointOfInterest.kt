package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class FavoritePointOfInterest(var poi: PointOfInterest? = null, var dateFavorite: Date? = Calendar.getInstance().time) :
    Parcelable
