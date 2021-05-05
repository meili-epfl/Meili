package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class VisitedPointOfInterest(var poi: PointOfInterest? = null, var dateVisited: Date? = Calendar.getInstance().time) :
    Parcelable
