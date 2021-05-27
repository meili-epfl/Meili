package com.github.epfl.meili.profile.friends

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
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase
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

        var getConnectionsClient: (Activity) -> ConnectionsClient =
            { a -> Nearby.getConnectionsClient(a) }
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
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.friend_succ),
                        Toast.LENGTH_SHORT
                    ).show()
                    connectionsClient.sendPayload(endpointId, Payload.fromBytes(ACK.toByteArray()))
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            AlertDialog.Builder(this@NearbyActivity)
                .setTitle(String.format(getString(R.string.accept_friend), info.endpointName))
                .setMessage(
                    String.format(
                        getString(
                            R.string.confirm_code,
                            info.authenticationToken
                        )
                    )
                )
                .setPositiveButton(getString(R.string.accept)) { _, _ ->
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
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.friend_abort),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                else -> Log.e(TAG, "CODE: ${result.status.statusCode}")
            }
        }

        override fun onDisconnected(endpointId: String) {}
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection(
                localUser.username,
                endpointId,
                connectionLifecycleCallback
            )
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
        findMyFriendButton.isEnabled = false

        if (!isLocationPermissionGranted(this)) {
            requestLocationPermission(this)
        } else if (!isLocationEnabled(applicationContext)) {
            requestLocation(this, { recreate() }, { finish() })
        } else {
            findMyFriendButton.isEnabled = true
        }

        localUser = Auth.getCurrentUser()!!
        database = FirestoreDatabase(
            String.format(
                FriendsListActivity.getFriendsDatabasePath(localUser.uid),
                localUser.uid
            ), Friend::class.java
        )
        connectionsClient = getConnectionsClient(this)
    }

    fun onNearbyButtonClick(view: View) {
        when (view) {
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
                getString(R.string.location_required),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            if (!isLocationEnabled(applicationContext)) {
                requestLocation(this, { recreate() }, { finish() })
            } else {
                recreate()
            }
        }
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            localUser.username,
            packageName,
            connectionLifecycleCallback,
            advertisingOptions
        )
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.looking_for_friend),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,

                    getString(R.string.error_find_friend),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions)
            .addOnFailureListener {

                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_find_friend),
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e(TAG, it.toString())
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectionsClient.stopDiscovery()
        connectionsClient.stopAdvertising()
    }
}