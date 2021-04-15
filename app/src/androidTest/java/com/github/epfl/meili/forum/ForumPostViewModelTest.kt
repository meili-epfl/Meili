package com.github.epfl.meili.forum;


import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.MockAuthenticationService
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.models.Post.Companion.toPost
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ForumPostViewModelTest {

    private val TEST_TEXT = "test text"
    private val TEST_TITLE = "test title"
    private val TEST_USERNAME = "test_username"
    private val TEST_EMAIL = "test@meili.com"
    private val postList = ArrayList<Post>()
    private val mockPost = Post("test id", TEST_USERNAME, TEST_TITLE, TEST_TEXT)
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
        Mockito.`when`(mockQuerySnapshot.documents.mapNotNull { it.toPost() }).thenReturn(postList)

        FirebasePostService.dbProvider = { mockFirestore }
    }

    @Before
    fun initiateAuthAndService() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            val mockAuthService = MockAuthenticationService()
            Auth.setAuthenticationService(mockAuthService)

            Auth.isLoggedIn.value = true
            Auth.email = TEST_EMAIL
            Auth.name = TEST_USERNAME
        }
    }

    @Test
    fun createNewPostTest() {
        UiThreadStatement.runOnUiThread {
            var mock_post2 = Post("0", "mockTester", "Paris", "VeryNice")

            NewPostViewModel.createNewPost(mock_post2.title, mock_post2.text)

            val expectedMessageList = ArrayList<Post>()
            expectedMessageList.add(mock_post1)
            expectedMessageList.add(mock_post2)

            assertEquals(expectedMessageList, ps.posts)
        }
    }

    @Test
    fun viewModelIsObserving() {
        UiThreadStatement.runOnUiThread {
            var mock_post2 = Post("0", "mockTester", "Paris", "VeryNice")
            NewPostViewModel.createNewPost(mock_post2.title, mock_post2.text)
            var expectedMessageList = ArrayList<Post>()


            expectedMessageList.add(mock_post1)
            expectedMessageList.add(mock_post2)


            assertEquals(mock_post2, PostViewModel.post.value)
            assertEquals(expectedMessageList, ps.posts)
            assertEquals(expectedMessageList, ForumViewModel.posts.value)

        }
    }

    @Test
    fun viewModelPostId() {
        UiThreadStatement.runOnUiThread {
            PostViewModel.post_id = "1234"
            assertEquals("1234", PostViewModel.post_id)
        }
    }

    @Test
    fun emptySyncDoesNotChangeAnything() {
        UiThreadStatement.runOnUiThread {
            val post = PostViewModel.post
            PostViewModel.update(ps, "")
            assertEquals(post, PostViewModel.post)

            val posts = ForumViewModel.posts
            ForumViewModel.update(ps, "")
            assertEquals(posts, ForumViewModel.posts)
        }
    }
}
