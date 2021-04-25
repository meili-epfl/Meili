package com.github.epfl.meili.nearby

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
import com.github.epfl.meili.models.User
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

data class Friend (
        var friendUid: String = ""
)

class NearbyActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "NearbyActivity"
        private val STRATEGY = Strategy.P2P_CLUSTER
    }

    private fun addFriend(friendUid: String) = database.addElement(friendUid, Friend(friendUid))

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val friendUid: String = payload.asBytes()!!.decodeToString()
            addFriend(friendUid)
            Toast.makeText(applicationContext, "Friendship successful!", Toast.LENGTH_SHORT).show()
            connectionsClient.disconnectFromEndpoint(endpointId)
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

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(localUser.username, packageName, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener { Toast.makeText(applicationContext, "Looking for your friend!", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(applicationContext, "Error finding friend", Toast.LENGTH_SHORT).show() }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions)
                .addOnFailureListener { Toast.makeText(applicationContext, "Error finding friend", Toast.LENGTH_SHORT).show() }
    }

    private lateinit var findMyFriendButton: Button
    private lateinit var database: FirestoreDatabase<Friend>
    private lateinit var localUser: User
    private lateinit var connectionsClient: ConnectionsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("Assertion failed")
        }

        localUser = Auth.getCurrentUser()!!
        database = FirestoreDatabase("friends/${localUser.uid}/friends", Friend::class.java)
        findMyFriendButton = findViewById(R.id.find_my_friend)
        connectionsClient = Nearby.getConnectionsClient(this)
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
}