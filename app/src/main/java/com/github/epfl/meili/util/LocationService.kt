package com.github.epfl.meili.util

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.epfl.meili.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes


/**
 * This service is supposing that permissions have already being granted before using it
 *
 * It's role is to provide location updates to the location listener passed as parameter in listenToLocationChanges function
 */
object LocationService {
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

    @SuppressLint("MissingPermission")
    fun listenToLocationChanges(locationManager: LocationManager, locationListener: LocationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, locationListener)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun requestLocation(context: Context, positiveListener: () -> Unit) {
        if (BuildConfig.DEBUG && isLocationEnabled(context)) {
            error("Assertion failed")
        }
        AlertDialog.Builder(context)
                .setMessage("Location Disabled")
                .setPositiveButton("Turn on location") { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    positiveListener()
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .show()
    }
}