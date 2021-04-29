package com.github.epfl.meili.map

import android.location.LocationManager
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.util.LocationService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    @get: Rule
    var testRule = ActivityScenarioRule(MapActivity::class.java)

    init {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any())).thenAnswer { mock(ListenerRegistration::class.java) }

        FirestoreDatabase.databaseProvider = { mockFirestore }
        LocationService.getLocationManager = { mock(LocationManager::class.java) }
    }

    @Throws(UiObjectNotFoundException::class)
    fun reactToPermission(device: UiDevice, text: String) {
        device.findObject(UiSelector().textContains(text)).click()
    }

/* This test doesn't work with gradlew since permissions are given by default and
    the permission request dialog box doesn't appear.
    However, the test does work on a real device (so I suppose on an emulator too, but haven't tested)
   @Test
   fun a_shouldDisplayPermissionRequestDialogAtStartup() {
       val device = UiDevice.getInstance(getInstrumentation());
       Thread.sleep(2000)
       assertViewWithTextIsVisible(device, "ALLOW")
       assertViewWithTextIsVisible(device, "DENY")
       // cleanup for the next test
       reactToPermission(device, "DENY")
   }
*/

    @Test
    fun b_locationButtonClickableAfterPermissionGrant() {
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