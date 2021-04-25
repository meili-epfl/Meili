package com.github.epfl.meili.nearby

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
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
            Toast.makeText(applicationContext, "RECEIVED $bytes", Toast.LENGTH_SHORT).show()
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            AlertDialog.Builder(this@NearbyActivity)
                    .setTitle("Accept connection to ${info.endpointName}")
                    .setMessage("Confirm the code matches on both devices: ${info.authenticationToken}")
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
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Toast.makeText(applicationContext, "CONNECTED!", Toast.LENGTH_SHORT).show()
                    connectionsClient.stopAdvertising()
                    connectionsClient.stopDiscovery()
                    this@NearbyActivity.endpointId = endpointId
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED ->
                    Toast.makeText(applicationContext, "REJECTED!", Toast.LENGTH_SHORT).show()
                else -> Log.e(TAG, "CODE: ${result.status.statusCode}")
            }
        }

        override fun onDisconnected(endpointId: String) {}
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection(localUserName, endpointId, connectionLifecycleCallback)
                    .addOnSuccessListener { Toast.makeText(applicationContext, "connection rq sent", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { Toast.makeText(applicationContext, "connection rq failed", Toast.LENGTH_SHORT).show() }
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(localUserName, packageName, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener { Toast.makeText(applicationContext, "advertising started", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(applicationContext, "advertising failed", Toast.LENGTH_SHORT).show() }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener { Toast.makeText(applicationContext, "discovery started", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(applicationContext, "discovery failed", Toast.LENGTH_SHORT).show() }
    }

    private lateinit var findButton: Button
    private lateinit var friendButton: Button
    private lateinit var disconnectButton: Button

    private lateinit var localUserName: String

    private lateinit var connectionsClient: ConnectionsClient

    private var endpointId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

        localUserName = "Meili User" // TODO

        findButton = findViewById(R.id.find)
        friendButton = findViewById(R.id.friend)
        disconnectButton = findViewById(R.id.disconnect)

        connectionsClient = Nearby.getConnectionsClient(this)
    }

    fun onNearbyButtonClick(view: View) {
        when(view) {
            findButton -> {
                startAdvertising()
                startDiscovery()
            }
            friendButton -> sendFriendRequest()
            disconnectButton -> disconnect()
        }
    }

    private fun sendFriendRequest() {
        if (endpointId != null) {
            val bytesPayload = Payload.fromBytes("Friend Request".toByteArray())
            connectionsClient.sendPayload(endpointId!!, bytesPayload)
        }
    }

    private fun disconnect() {
        if (endpointId != null) {
            connectionsClient.disconnectFromEndpoint(endpointId!!)
        }
    }
}