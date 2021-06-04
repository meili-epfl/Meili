package com.github.epfl.meili.map

import android.location.LocationManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.posts.feed.FeedActivity
import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.profile.friends.FriendsListActivityTest
import com.github.epfl.meili.util.LandmarkDetectionService
import com.github.epfl.meili.util.LocationService
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.storage.FirebaseStorage
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    companion object {
        private const val TEST_LANDMARK = "LANDMARK"
    }

    @get: Rule
    var testRule = ActivityScenarioRule(MapActivity::class.java)

    private val landmarkSuccessListenerCaptor =
        ArgumentCaptor.forClass(OnSuccessListener::class.java) as
                ArgumentCaptor<OnSuccessListener<List<FirebaseVisionCloudLandmark>>>

    private val landmarkFailureListenerCaptor =
        ArgumentCaptor.forClass(OnFailureListener::class.java)

    init {
        setupMocks()
        setupLandmarkServiceMocks()
    }

    private fun setupLandmarkServiceMocks() {
        val mockTask = mock(Task::class.java) as Task<List<FirebaseVisionCloudLandmark>>
        `when`(mockTask.addOnSuccessListener(landmarkSuccessListenerCaptor.capture())).thenReturn(
            mockTask
        )
        `when`(mockTask.addOnFailureListener(landmarkFailureListenerCaptor.capture())).thenReturn(
            mockTask
        )

        val mockFirebaseVision = mock(FirebaseVision::class.java)
        val mockLandmarkDetector = mock(FirebaseVisionCloudLandmarkDetector::class.java)
        `when`(mockFirebaseVision.visionCloudLandmarkDetector).thenReturn(mockLandmarkDetector)
        `when`(mockLandmarkDetector.detectInImage(any())).thenReturn(mockTask)

        LandmarkDetectionService.firebaseVisionImage =
            { _, _ -> mock(FirebaseVisionImage::class.java) }
        LandmarkDetectionService.firebaseVision = { mockFirebaseVision }
    }

    private fun setupMocks() {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { mock(ListenerRegistration::class.java) }


        val mockDocument = mock(DocumentReference::class.java)
        `when`(mockFirestore.document(any())).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(mock(Task::class.java) as Task<DocumentSnapshot>)
        `when`(mockCollection.document(any())).thenReturn(mockDocument)

        val mockAuthenticationService = MockAuthenticationService()
        mockAuthenticationService.signInIntent(null)
        Auth.authService = mockAuthenticationService

        FirestoreDatabase.databaseProvider = { mockFirestore }
        FirestoreDocumentService.databaseProvider = { mockFirestore }
        FirebaseStorageService.storageProvider = { mock(FirebaseStorage::class.java) }
        LocationService.getLocationManager = { mock(LocationManager::class.java) }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Throws(UiObjectNotFoundException::class)
    fun reactToPermission(device: UiDevice, text: String) {
        device.findObject(UiSelector().textContains(text)).click()
    }

    @Test
    fun locationButtonClickableAfterPermissionGrant() {
        PermissionGranter.allowPermissionsIfNeeded("android.permissions.ACCESS_FINE_LOCATION")
        val imageView = onView(
            allOf(
                withContentDescription("My Location"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView.perform(click())
    }

    @Test
    fun goToProfileTest() {
        onView(withId(R.id.profile_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.qualifiedName))
    }

    @Test
    fun goToFeedTest() {
        onView(withId(R.id.feed_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FeedActivity::class.qualifiedName))
    }

    @Test
    fun clickOnLensCamera() {
        onView(withId(R.id.lens_camera)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(CameraActivity::class.qualifiedName))
    }

    @Test
    fun landmarksTest() {
        onView(withId(R.id.lens_dismiss_landmark)).check(matches(not(isDisplayed())))

        val mockLandmark = mock(FirebaseVisionCloudLandmark::class.java)
        `when`(mockLandmark.landmark).thenReturn(TEST_LANDMARK)

        lateinit var viewModel: MapActivityViewModel

        testRule.scenario.onActivity {
            val lazyViewModel: Lazy<MapActivityViewModel> = it.viewModels()
            viewModel = lazyViewModel.value
        }

        viewModel.handleCameraResponse(Uri.EMPTY)

        runOnUiThread {
            landmarkSuccessListenerCaptor.value.onSuccess(listOf(mockLandmark))
        }

        onView(withText(TEST_LANDMARK)).check(matches(isDisplayed()))
        onView(withId(R.id.lens_dismiss_landmark)).perform(click())

        onView(withId(R.id.lens_dismiss_landmark)).check(matches(not(isDisplayed())))

        runOnUiThread {
            landmarkFailureListenerCaptor.value.onFailure(IllegalArgumentException(""))
        }
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}