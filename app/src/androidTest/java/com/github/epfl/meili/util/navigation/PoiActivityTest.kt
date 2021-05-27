package com.github.epfl.meili.util.navigation

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.map.PointOfInterestStatus
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiInfoActivity
import com.github.epfl.meili.posts.forum.ForumActivity
import com.github.epfl.meili.review.ReviewsActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class PoiActivityTest {
    companion object {
        private val TEST_POI = PointOfInterest(0.0, 0.0, "NAME", "UID")
    }

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        PoiInfoActivity::class.java
    ).putExtra(MapActivity.POI_KEY, TEST_POI)
        .putExtra(MapActivity.POI_STATUS_KEY, PointOfInterestStatus.VISITED)

    @get:Rule
    var testRule: ActivityScenarioRule<PoiInfoActivity> = ActivityScenarioRule(intent)

    init {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        val mockQuery = mock(Query::class.java)
        `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
        `when`(mockCollection.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery)
        `when`(mockCollection.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration::class.java))
        `when`(mockQuery.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration::class.java))

        FirestoreDatabase.databaseProvider = { mockFirestore }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun poiInfoActivityLaunches() {
        onView(withId(R.id.poi_info_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(PoiInfoActivity::class.qualifiedName))
    }

    @Test
    fun forumActivityLaunches() {
        onView(withId(R.id.forum_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ForumActivity::class.qualifiedName))
    }

    @Test
    fun chatActivityLaunches() {
        onView(withId(R.id.chat_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ChatLogActivity::class.qualifiedName))
    }

    @Test
    fun reviewsActivityLaunches() {
        onView(withId(R.id.reviews_activity)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ReviewsActivity::class.qualifiedName))
    }
}