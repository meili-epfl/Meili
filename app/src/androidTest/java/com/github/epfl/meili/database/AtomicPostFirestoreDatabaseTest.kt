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
    private val post1 = Post("author1", "title1", "text1")
    private val post2 = Post("author2", "title2", "text2")

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
    fun upvoteTest() {
        db.upDownVote("post1", "me", true)
    }

    @Test
    fun downvoteTest() {
        db.upDownVote("post1", "me", false)
    }

}