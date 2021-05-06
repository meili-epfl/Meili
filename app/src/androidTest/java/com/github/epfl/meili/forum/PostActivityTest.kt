package com.github.epfl.meili.forum


import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.Comment
import com.github.epfl.meili.util.MockAuthenticationService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class PostActivityTest {

    companion object {
        private const val TEST_POI_KEY = "POI_KEY"
        private const val TEST_ID = "ID"
        private val TEST_POST = Post("AUTHOR", "TITLE", "TEXT")
    }

    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockComments: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)

    private val mockSnapshotBeforeAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
    private val mockSnapshotAfterAddition: QuerySnapshot = Mockito.mock(QuerySnapshot::class.java)

    private val mockAuthenticationService = MockAuthenticationService()

    private lateinit var database: AtomicPostFirestoreDatabase
    private lateinit var commentsDatabase: FirestoreDatabase<Comment>

    private val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext, PostActivity::class.java)
            .putExtra(Post.TAG, TEST_POST)
            .putExtra(PostActivity.POST_ID, TEST_ID)
            .putExtra(MapActivity.POI_KEY, TEST_POI_KEY)

    @get:Rule
    var testRule: ActivityScenarioRule<PostActivity> = ActivityScenarioRule(intent)

    @Before
    fun initIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    init {
        setupMocks()
        setupPostActivityMocks()
    }

    private fun setupMocks() {
        Mockito.`when`(mockFirestore.collection("forum/${TEST_POI_KEY}/posts"))
            .thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(any())).thenAnswer { invocation ->
            database = invocation.arguments[0] as AtomicPostFirestoreDatabase
            Mockito.mock(ListenerRegistration::class.java)
        }
        Mockito.`when`(mockCollection.document(ArgumentMatchers.contains(TEST_ID)))
            .thenReturn(mockDocument)

        Mockito.`when`(mockFirestore.collection("forum/${TEST_POI_KEY}/posts/${TEST_ID}/comments"))
            .thenReturn(mockComments)
        Mockito.`when`(mockComments.addSnapshotListener(any())).thenAnswer { invocation ->
            commentsDatabase = invocation.arguments[0] as FirestoreDatabase<Comment>
            Mockito.mock(ListenerRegistration::class.java)
        }
        Mockito.`when`(mockComments.document(ArgumentMatchers.contains(TEST_ID)))
            .thenReturn(mockDocument)

        Mockito.`when`(mockSnapshotBeforeAddition.documents).thenReturn(ArrayList<DocumentSnapshot>())

        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn(TEST_ID)
        Mockito.`when`(mockDocumentSnapshot.toObject(Post::class.java))
            .thenReturn(TEST_POST)
        Mockito.`when`(mockSnapshotAfterAddition.documents).thenReturn(listOf(mockDocumentSnapshot))

        mockAuthenticationService.setMockUid(TEST_ID)
        mockAuthenticationService.setUsername(TEST_ID)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        FirebaseStorageService.storageProvider = { Mockito.mock(FirebaseStorage::class.java) }
        Auth.authService = mockAuthenticationService
    }

    private fun setupPostActivityMocks() {
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

    @Test
    fun checkPostShown() {
        onView(withId(R.id.post_author)).check(matches(withText(containsString(TEST_POST.author))))
        onView(withId(R.id.post_title)).check(matches(withText(containsString(TEST_POST.title))))
        onView(withId(R.id.post_text)).check(matches(withText(containsString(TEST_POST.text))))
    }
}
