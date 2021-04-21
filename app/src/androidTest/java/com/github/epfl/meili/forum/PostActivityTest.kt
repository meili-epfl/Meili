package com.github.epfl.meili.forum


import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.storage.FirebaseStorageService
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class PostActivityTest {

    companion object {
        private const val TEST_ID = "ID"
        private val TEST_POST = Post("AUTHOR", "TITLE", "TEXT")
    }

    private val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext, PostActivity::class.java)
            .putExtra(Post.TAG, TEST_POST)
            .putExtra(PostActivity.POST_ID, TEST_ID)

    init {
        val mockFirebase = Mockito.mock(FirebaseStorage::class.java)
        val mockReference = Mockito.mock(StorageReference::class.java)
        Mockito.`when`(mockFirebase.getReference(anyString())).thenReturn(mockReference)

        val mockUploadTask = Mockito.mock(UploadTask::class.java)
        Mockito.`when`(mockReference.putBytes(any())).thenReturn(mockUploadTask)

        val mockStorageTask = Mockito.mock(StorageTask::class.java)
        Mockito.`when`(mockUploadTask.addOnSuccessListener(any())).thenReturn(mockStorageTask as StorageTask<UploadTask.TaskSnapshot>?)

        val mockTask = Mockito.mock(Task::class.java)
        Mockito.`when`(mockReference.downloadUrl).thenReturn(mockTask as Task<Uri>?)
        Mockito.`when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)

        FirebaseStorageService.storageProvider = { mockFirebase }
    }

    @get:Rule
    var testRule: ActivityScenarioRule<PostActivity> = ActivityScenarioRule(intent)

    @Test
    fun checkPostShown() {
        onView(withId(R.id.post_author)).check(matches(withText(containsString(TEST_POST.author))))
        onView(withId(R.id.post_title)).check(matches(withText(containsString(TEST_POST.title))))
        onView(withId(R.id.post_text)).check(matches(withText(containsString(TEST_POST.text))))
    }
}
