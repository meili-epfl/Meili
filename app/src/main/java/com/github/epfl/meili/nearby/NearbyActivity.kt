package com.github.epfl.meili.nearby

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*


class NearbyActivity : AppCompatActivity() {
    // TODO supports only a single connection for now

    companion object {
        private const val TAG = "NearbyActivity"
        private val STRATEGY = Strategy.P2P_CLUSTER
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val bytes: ByteArray = payload.asBytes()!!
            Log.e(TAG, "RECEIVED: $bytes")
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            AlertDialog.Builder(applicationContext)
                    .setTitle("Accept connection to ${info.endpointName}")
                    .setMessage("Confirm the code matches on both devices: ${info.authenticationToken}")
                    .setPositiveButton("Accept") { _, _ ->
                        Nearby.getConnectionsClient(applicationContext)
                                .acceptConnection(endpointId, payloadCallback)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        Nearby.getConnectionsClient(applicationContext).rejectConnection(endpointId)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.e(TAG, "CONNECTED to $endpointId")
                    this@NearbyActivity.endpointId = endpointId
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Log.e(TAG, "REJECTED!")
                else -> Log.e(TAG, "CODE: ${result.status.statusCode}")
            }
        }

        override fun onDisconnected(p0: String) {
            Log.e(TAG, "Disconnected from the endpoint")
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Nearby.getConnectionsClient(applicationContext)
                    .requestConnection(localUserName, endpointId, connectionLifecycleCallback)
                    .addOnSuccessListener { Log.e(TAG, "connection request successful") }
                    .addOnFailureListener { Log.e(TAG, "connection request failed") }
        }

        override fun onEndpointLost(p0: String) {
            Log.e(TAG, "A previously discovered endpoint has gone away")
        }
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(applicationContext)
                .startAdvertising(localUserName, packageName, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener { Log.e(TAG, "advertising successful") }
                .addOnFailureListener { Log.e(TAG, "advertising failed") }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(applicationContext)
                .startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener { Log.e(TAG, "discovery successful") }
                .addOnFailureListener { Log.e(TAG, "discovery failed") }
    }

    private lateinit var advertiseButton: Button
    private lateinit var findButton: Button
    private lateinit var friendButton: Button
    private lateinit var disconnectButton: Button

    private lateinit var localUserName: String

    private var endpointId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

        localUserName = "Meili User" // TODO
        advertiseButton = findViewById(R.id.advertise)
        findButton = findViewById(R.id.find)
        friendButton = findViewById(R.id.friend)
        disconnectButton = findViewById(R.id.disconnect)
    }

    fun onNearbyButtonClick(view: View) {
        when(view) {
            advertiseButton -> startAdvertising()
            findButton -> startDiscovery()
            friendButton -> sendFriendRequest()
            disconnectButton -> disconnect()
        }
    }

    private fun sendFriendRequest() {
        if (endpointId != null) {
            val bytesPayload = Payload.fromBytes("Friend Request".toByteArray())
            Nearby.getConnectionsClient(applicationContext).sendPayload(endpointId!!, bytesPayload)
        }
    }

    private fun disconnect() {
        Nearby.getConnectionsClient(applicationContext).disconnectFromEndpoint(endpointId!!)
    }
}