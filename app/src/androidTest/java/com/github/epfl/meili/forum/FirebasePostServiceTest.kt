package com.github.epfl.meili.forum

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class FirebasePostServiceTest {

    private val postList = ArrayList<Post>()
    private val mockPost = Post("test id", "TEST_USERNAME", "TEST_TITLE", "TEST_TEXT")
    private val mockList = ArrayList<DocumentSnapshot>()

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockQuerySnapshot: QuerySnapshot
    var mock_post1 = Post("FAKE_ID", "FAKE_AUTHOR", "FAKE_TITLE", "FAKE_TEXT")
    lateinit var ps: MockPostService

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ps = MockPostService()
            PostViewModel.changePostService(ps)
            PostViewModel.setID("0")
            ForumViewModel.changePostService(ps)
            NewPostViewModel.changePostService(ps)

        }
    }


    @Before
    fun initializeMockDatabase() {
        postList.add(mockPost)

        mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
        mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        mockCollectionReference = Mockito.mock(CollectionReference::class.java)
        mockDocumentReference = Mockito.mock(DocumentReference::class.java)
        mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
        val mockTask = Mockito.mock(Task::class.java)

        Mockito.`when`(mockFirestore.collection("posts")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(ArgumentMatchers.any()))
            .thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.get()).thenAnswer { mockDocumentSnapshot }
        Mockito.`when`(mockCollectionReference.get()).thenAnswer { mockQuerySnapshot }
        Mockito.`when`(mockCollectionReference.add(ArgumentMatchers.any())).thenAnswer {
            mockList.add(mockDocumentSnapshot)
            mockTask  // Needs a Task, so I put a mock Task
        }
        Mockito.`when`(mockQuerySnapshot.documents.mapNotNull { it.toObject(Post::class.java) }).thenReturn(postList)

        FirebasePostService.dbProvider = { mockFirestore }
    }

    @Test
    fun addPostTest() {
        val fbPostService = FirebasePostService()
        fbPostService.addPost("author", "title", "text")

        assertEquals("0", PostViewModel.post_id)
    }

    @Test
    fun getPostsTest() {
        runBlocking {
            val fbPostService = FirebasePostService()
            val list = fbPostService.getPosts()

            assertEquals(emptyList<Post>(), list)
        }
    }

    @Test
    fun getPostFromIdTest() {
        runBlocking {
            val fbPostService = FirebasePostService()
            assertNull(fbPostService.getPostFromId(null))

            assertNull(fbPostService.getPostFromId(""))
        }
    }
}