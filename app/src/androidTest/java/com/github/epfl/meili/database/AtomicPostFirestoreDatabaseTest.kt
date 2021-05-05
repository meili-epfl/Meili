package com.github.epfl.meili.database

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.google.firebase.firestore.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class AtomicPostFirestoreDatabaseTest {
    private lateinit var db: AtomicPostFirestoreDatabase

    private val postId = "test_id"
    private val upvoters = arrayListOf<String>("upvoter")
    private val downvoters = arrayListOf<String>("downvoter")

    private val post = Post("me", "title", "text")
    private val upvotedPost = Post("me", "title", "text", upvoters)
    private val downvotedPost = Post("me", "title", "text", downvoters = downvoters)


    private val mockFirestore: FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
    private val mockCollection: CollectionReference = Mockito.mock(CollectionReference::class.java)
    private val mockDocument: DocumentReference = Mockito.mock(DocumentReference::class.java)
    private val mockListenerRegistration: ListenerRegistration = Mockito.mock(ListenerRegistration::class.java)

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    init {
        setupMocks()
    }

    private fun setupMocks() {
        Mockito.`when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollection)
        Mockito.`when`(mockCollection.addSnapshotListener(ArgumentMatchers.any())).thenAnswer { invocation ->
            mockListenerRegistration
        }
        Mockito.`when`(mockCollection.document(Mockito.any())).thenReturn(mockDocument)

        // Inject dependencies
        FirestoreDatabase.databaseProvider = { mockFirestore }
        db = AtomicPostFirestoreDatabase("test_path")
    }

    private fun getMockDocumentSnapshot(id: String, poi: PointOfInterest): DocumentSnapshot {
        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn(id)
        Mockito.`when`(mockDocumentSnapshot.toObject(PointOfInterest::class.java)).thenReturn(poi)
        return mockDocumentSnapshot
    }

    @Test
    fun upvotePostTest() {
        val mockDocumentReference = Mockito.mock(DocumentReference::class.java)

        Mockito.`when`(mockCollection.document(Mockito.anyString())).then {
            assertEquals(it.arguments[0], postId)
            return@then mockDocumentReference
        }
        Mockito.`when`(mockDocumentReference.set(Mockito.any())).then {
            assertEquals(it.arguments[0], upvotedPost)
            return@then null
        }

        mockCollection
        db.upDownVote(postId, upvoters[0], true)
    }

    @Test
    fun downvotePostTest() {
        val mockDocumentReference = Mockito.mock(DocumentReference::class.java)

        Mockito.`when`(mockCollection.document(Mockito.anyString())).then {
            assertEquals(it.arguments[0], postId)
            return@then mockDocumentReference
        }
        Mockito.`when`(mockDocumentReference.set(Mockito.any())).then {
            assertEquals(it.arguments[0], downvotedPost)
            return@then null
        }

        mockCollection
        db.upDownVote(postId, downvoters[0], false)
    }

/*    @Test
    fun onDestroyTest() {
        Mockito.`when`(mockListenerRegistration.remove()).then {
            return@then null
        }

        db.onDestroy()
    }

    @Test
    fun onEventErrorTest() {
        val error = Mockito.mock(FirebaseFirestoreException::class.java)
        db.onEvent(null, error)
    }

    @Test
    fun onEventWithoutSnapshotTest() {
        db.onEvent(null, null)
    }

    @Test
    fun onEventTest() {
        val mockDocumentSnapshot1 = getMockDocumentSnapshot(poi1.uid, poi1)
        val mockDocumentSnapshot2 = getMockDocumentSnapshot(poi2.uid, poi2)

        val documents = ArrayList<DocumentSnapshot>()
        documents.add(mockDocumentSnapshot1)
        documents.add(mockDocumentSnapshot2)

        val expectedElements = HashMap<String, PointOfInterest>()
        expectedElements[poi1.uid] = poi1
        expectedElements[poi2.uid] = poi2

        val mockSnapshot = Mockito.mock(QuerySnapshot::class.java)
        Mockito.`when`(mockSnapshot.documents).thenReturn(documents)

        db.onEvent(mockSnapshot, null)
        assertEquals(db.elements, expectedElements)
    }*/
}