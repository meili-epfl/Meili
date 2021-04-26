package com.github.epfl.meili.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.epfl.meili.BuildConfig

object LocationPermissionService {

    private const val REQUEST_CODE = 10100

    fun isLocationPermissionGranted(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        if (BuildConfig.DEBUG && isLocationPermissionGranted(activity)) {
            error("Assertion failed")
        }
        
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }
}