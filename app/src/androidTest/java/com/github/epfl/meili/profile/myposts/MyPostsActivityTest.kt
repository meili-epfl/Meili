package com.github.epfl.meili.profile.myposts

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.User
import com.github.epfl.meili.posts.FeedActivityTest
import com.github.epfl.meili.posts.PostListActivity
import com.github.epfl.meili.posts.PostListViewModel
import com.github.epfl.meili.posts.feed.FeedViewModel
import com.github.epfl.meili.posts.forum.ForumActivity
import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.profile.favoritepois.FavoritePoisActivity
import com.github.epfl.meili.profile.favoritepois.FavoritePoisActivityTest
import com.github.epfl.meili.profile.friends.UserInfoService
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.hamcrest.CoreMatchers
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
        mockAuthenticationService.signInIntent()
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