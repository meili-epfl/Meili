package com.github.epfl.meili.posts

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.posts.feed.FeedActivity
import com.github.epfl.meili.posts.feed.FeedViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class FeedActivityTest {

    companion object {
        val MOCK_USER_LOCATION = LatLng(0.0, 0.0)
        val MOCK_POI = PointOfInterest(0.1, 0.0, "POI", "UID")
    }

    @get:Rule
    var rule = ActivityScenarioRule(FeedActivity::class.java)

    init {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        val mockQuery = mock(Query::class.java)

        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.whereIn(anyString(), anyList())).thenReturn(mockQuery)
        `when`(mockQuery.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration::class.java))

        FirestoreDatabase.databaseProvider = { mockFirestore }
    }

    @Test
    fun feedRecyclerViewDisplayed() {
        onView(withId(R.id.feed_recycler_view)).check(matches(isDisplayed()))

        rule.scenario.onActivity {
            val viewModel = it.viewModel as FeedViewModel
            viewModel.lastUserLocation = MOCK_USER_LOCATION

            viewModel.onSuccessPoiReceived(listOf(MOCK_POI))
        }
    }
}