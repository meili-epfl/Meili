package com.github.epfl.meili.database

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.poi.PointOfInterest
import com.google.firebase.firestore.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class FirestoreDatabaseTest {
    private lateinit var db: FirestoreDatabase<PointOfInterest>
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")

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
        db = FirestoreDatabase("test_path", PointOfInterest::class.java)
    }

    private fun getMockDocumentSnapshot(id: String, poi: PointOfInterest): DocumentSnapshot {
        val mockDocumentSnapshot: DocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn(id)
        Mockito.`when`(mockDocumentSnapshot.toObject(PointOfInterest::class.java)).thenReturn(poi)
        return mockDocumentSnapshot
    }

    @Test
    fun addElementTest() {
        val mockDocumentReference = Mockito.mock(DocumentReference::class.java)

        Mockito.`when`(mockCollection.document(Mockito.anyString())).then {
            assertEquals(it.arguments[0], poi1.uid)
            return@then mockDocumentReference
        }
        Mockito.`when`(mockDocumentReference.set(Mockito.any())).then {
            assertEquals(it.arguments[0], poi1)
            return@then null
        }

        mockCollection
        db.addElement(poi1.uid, poi1)
    }

    @Test
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
    }
}