package com.github.epfl.meili.friends

import com.github.epfl.meili.database.FirestoreDocumentService
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class UserInfoServiceTest {
    private val TEST_UID
    @Test
    fun getUserInformationTest(){
        val mockDocumentService = Mockito.mock(FirestoreDocumentService::class.java)
        Mockito.`when`(mockDocumentService.getDocument(ArgumentMatchers.anyString()))
    }
}