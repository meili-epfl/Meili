package com.github.epfl.meili.nearby

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.models.User
import com.github.epfl.meili.util.LocationService.isLocationEnabled
import com.github.epfl.meili.util.LocationService.isLocationPermissionGranted
import com.github.epfl.meili.util.LocationService.requestLocation
import com.github.epfl.meili.util.LocationService.requestLocationPermission
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "NearbyActivity"
        private val STRATEGY = Strategy.P2P_CLUSTER
        private const val ACK = "ACK"

        var getConnectionsClient: (Activity) -> ConnectionsClient = { a -> Nearby.getConnectionsClient(a) }
    }

    private lateinit var findMyFriendButton: Button
    private lateinit var database: FirestoreDatabase<Friend>
    private lateinit var localUser: User
    private lateinit var connectionsClient: ConnectionsClient

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            when (val payloadString = payload.asBytes()!!.decodeToString()) {
                ACK -> connectionsClient.disconnectFromEndpoint(endpointId)
                else -> {
                    database.addElement(payloadString, Friend(payloadString))
                    Toast.makeText(applicationContext, "Friendship successful!", Toast.LENGTH_SHORT).show()
                    connectionsClient.sendPayload(endpointId, Payload.fromBytes(ACK.toByteArray()))
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            AlertDialog.Builder(this@NearbyActivity)
                    .setTitle("Accept friendship with ${info.endpointName}")
                    .setMessage("Confirm the code matches on both devices to finalize your friendship: ${info.authenticationToken}")
                    .setPositiveButton("Accept") { _, _ ->
                        connectionsClient.acceptConnection(endpointId, payloadCallback)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        connectionsClient.rejectConnection(endpointId)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            connectionsClient.stopAdvertising()
            connectionsClient.stopDiscovery()
            findMyFriendButton.isEnabled = true
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    val uidPayload = Payload.fromBytes(localUser.uid.toByteArray())
                    connectionsClient.sendPayload(endpointId, uidPayload)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED ->
                    Toast.makeText(applicationContext, "Friendship aborted!", Toast.LENGTH_SHORT).show()
                else -> Log.e(TAG, "CODE: ${result.status.statusCode}")
            }
        }

        override fun onDisconnected(endpointId: String) {}
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection(localUser.username, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("Assertion failed")
        }

        findMyFriendButton = findViewById(R.id.find_my_friend)

        if (!isLocationPermissionGranted(this)) {
            findMyFriendButton.isEnabled = false
            requestLocationPermission(this)
        } else if (!isLocationEnabled(applicationContext)) {
            findMyFriendButton.isEnabled = false
            requestLocation(applicationContext) { findMyFriendButton.isEnabled = true }
        }

        localUser = Auth.getCurrentUser()!!
        database = FirestoreDatabase("friends/${localUser.uid}/friends", Friend::class.java)
        connectionsClient = getConnectionsClient(this)
    }

    fun onNearbyButtonClick(view: View) {
        when(view) {
            findMyFriendButton -> {
                startAdvertising()
                startDiscovery()
                findMyFriendButton.isEnabled = false
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!isLocationPermissionGranted(this)) {
            Toast.makeText(
                    applicationContext,
                    "Location is required for this feature",
                    Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            if (!isLocationEnabled(applicationContext)) {
                requestLocation(applicationContext) { findMyFriendButton.isEnabled = true }
            } else {
                findMyFriendButton.isEnabled = true
            }
        }
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(localUser.username, packageName, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener { Toast.makeText(applicationContext, "Looking for your friend!", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(applicationContext, "Error finding friend", Toast.LENGTH_SHORT).show() }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions)
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Error finding friend", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, it.toString())
                }
    }
}