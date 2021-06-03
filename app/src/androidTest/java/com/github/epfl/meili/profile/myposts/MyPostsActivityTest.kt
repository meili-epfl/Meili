package com.github.epfl.meili.profile.myposts

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.util.MockAuthenticationService
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class MyPostsActivityTest {
    companion object {
        private const val TEST_UID = "UID"
    }

    private val mockAuthenticationService = MockAuthenticationService()

    private val mockFirestore = mock(FirebaseFirestore::class.java)
    private val mockCollection = mock(CollectionReference::class.java)
    private val mockQuery = mock(Query::class.java)

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        MyPostsActivity::class.java
    )
        .putExtra(ProfileActivity.USER_KEY, TEST_UID)

    @get:Rule
    var rule: ActivityScenarioRule<MyPostsActivity> = ActivityScenarioRule(intent)

    @Before
    fun initIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    init {
        mockAuthenticationService.signInIntent(null)
        setupMocks()
    }

    private fun setupMocks() {
        `when`(mockFirestore.collection("forum")).thenReturn(mockCollection)
        `when`(mockCollection.whereEqualTo(Post.AUTHOR_UID_FIELD, TEST_UID)).thenReturn(mockQuery)
        `when`(mockQuery.addSnapshotListener(any())).thenReturn(mock(ListenerRegistration::class.java))

        FirestoreDatabase.databaseProvider = { mockFirestore }
    }

    @Test
    fun myPostsRecyclerViewDisplayed() {
        onView(withId(R.id.my_posts_recycler_view)).check(matches(isDisplayed()))
    }
}