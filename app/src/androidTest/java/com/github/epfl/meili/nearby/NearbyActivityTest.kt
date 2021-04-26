package com.github.epfl.meili.nearby

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class NearbyActivityTest {

    companion object {
        private const val MOCK_UID = "UID"
        private const val MOCK_USERNAME = "Meili User"
        private const val MOCK_FRIEND_USERNAME = "Friendly User"
        private const val MOCK_FRIEND_UID = "Friendly UID"

        private const val MOCK_ENDPOINT_ID = "ENDPOINT_ID"
    }

    private val connectionLifecycleCallbackCaptor = ArgumentCaptor.forClass(ConnectionLifecycleCallback::class.java)
    private val endpointDiscoveryCallbackCaptor = ArgumentCaptor.forClass(EndpointDiscoveryCallback::class.java)
    private val payloadCallbackCaptor = ArgumentCaptor.forClass(PayloadCallback::class.java)

    init {
        setupNearbyMocks()
        setupFirestoreMocks()
    }

    private fun setupFirestoreMocks() {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        `when`(mockFirestore.collection("friends/${MOCK_UID}/friends")).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { mock(ListenerRegistration::class.java) }
        `when`(mockCollection.document(MOCK_FRIEND_UID)).thenReturn(mockDocument)

        FirestoreDatabase.databaseProvider = { mockFirestore }
    }

    private fun setupNearbyMocks() {
        val mockClient = mock(ConnectionsClient::class.java)
        val mockTask = mock(Task::class.java)

        `when`(mockClient.startAdvertising(any(), any(), connectionLifecycleCallbackCaptor.capture(), any())).thenReturn(mockTask as Task<Void>?)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)

        `when`(mockClient.startDiscovery(any(), endpointDiscoveryCallbackCaptor.capture(), any())).thenReturn(mockTask)

        `when`(mockClient.acceptConnection(eq(MOCK_ENDPOINT_ID), payloadCallbackCaptor.capture())).thenReturn(mockTask)

        val mockAuth = MockAuthenticationService()
        mockAuth.setMockUid(MOCK_UID)
        mockAuth.setUsername(MOCK_USERNAME)
        mockAuth.signInIntent()

        NearbyActivity.getConnectionsClient = { mockClient }
        Auth.authService = mockAuth
    }

    @get:Rule
    var rule: ActivityScenarioRule<NearbyActivity> = ActivityScenarioRule(NearbyActivity::class.java)

    @Test
    fun testNearbyConnection() {
        onView(withId(R.id.find_my_friend)).perform(click())
        onView(withId(R.id.find_my_friend)).check(matches(isNotEnabled()))

        endpointDiscoveryCallbackCaptor.value.onEndpointFound(MOCK_ENDPOINT_ID, DiscoveredEndpointInfo("", ""))

        val connectionLifecycleCallback = connectionLifecycleCallbackCaptor.value
        runOnUiThread {
            connectionLifecycleCallback.onConnectionInitiated(MOCK_ENDPOINT_ID, ConnectionInfo(MOCK_FRIEND_USERNAME, "", false))
        }

        val device = UiDevice.getInstance(getInstrumentation())
        val acceptButton = device.findObject(UiSelector().textContains("Accept"))
        if (acceptButton.exists()) {
            acceptButton.click()
        }

        runOnUiThread {
            connectionLifecycleCallback.onConnectionResult(MOCK_ENDPOINT_ID, ConnectionResolution(Status(ConnectionsStatusCodes.STATUS_OK)))
        }

        onView(withId(R.id.find_my_friend)).check(matches(isEnabled()))

        val uidPayload = Payload.fromBytes(MOCK_FRIEND_UID.toByteArray())

        runOnUiThread {
            payloadCallbackCaptor.value.onPayloadReceived(MOCK_ENDPOINT_ID, uidPayload)
        }
    }
}