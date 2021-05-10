package com.github.epfl.meili.friends

import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.friends.UserInfoService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class UserInfoServiceTest {
    private val TEST_UID = "TEST_ID"
    private val TEST_NAME = "TEST_NAME"

    @Test
    fun getUserInformationTest() {
        val testUser = User(TEST_UID, TEST_NAME)

        val mockDocument = Mockito.mock(DocumentSnapshot::class.java)
        Mockito.`when`(mockDocument.exists()).thenReturn(true)
        Mockito.`when`(mockDocument.toObject(User::class.java)).thenReturn(testUser)
        val mockTask = Mockito.mock(Task::class.java)
        Mockito.`when`(mockTask.addOnSuccessListener(Mockito.any())).then {
            val listener = it.arguments[0] as (DocumentSnapshot) -> Unit
            listener(mockDocument)
            return@then null
        }
        val mockDocumentService = Mockito.mock(FirestoreDocumentService::class.java)
        Mockito.`when`(mockDocumentService.getDocument(Mockito.anyString())).thenReturn(mockTask as Task<DocumentSnapshot>)

        val testList = ArrayList<String>()
        testList.add(TEST_UID)
        val expectedResult = HashMap<String, User>()
        expectedResult[TEST_UID] = testUser

        val onSuccess = { it:Map<String, User> -> assertEquals(it, expectedResult)}

        UserInfoService().getUserInformation(testList, onSuccess ,{assert(false)})
    }
}