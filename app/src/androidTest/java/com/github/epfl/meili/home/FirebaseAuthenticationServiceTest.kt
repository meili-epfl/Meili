package com.github.epfl.meili.home

import android.net.Uri
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.database.FirebaseStorageService
import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.ProfileActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class FirebaseAuthenticationServiceTest {
    private lateinit var fauth: FirebaseAuthenticationService

    @get:Rule
    var testRule = ActivityScenarioRule(ProfileActivity::class.java)

    init {
        setupMocks()
        setupStorageMocks()
    }

    private fun setupMocks() {
        val mockFirestore = mock(FirebaseFirestore::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        `when`(mockFirestore.document(Mockito.any())).thenReturn(mockDocument)

        val mockTask = mock(Task::class.java)
        `when`(mockDocument.get()).thenReturn(mockTask as Task<DocumentSnapshot>?)

        FirestoreDocumentService.databaseProvider = { mockFirestore }
    }

    private fun setupStorageMocks() {
        val mockFirebase = mock(FirebaseStorage::class.java)
        val mockReference = mock(StorageReference::class.java)
        `when`(mockFirebase.getReference(ArgumentMatchers.anyString())).thenReturn(mockReference)

        val mockUploadTask = mock(UploadTask::class.java)
        `when`(mockReference.putBytes(ArgumentMatchers.any())).thenReturn(mockUploadTask)

        val mockStorageTask = mock(StorageTask::class.java)
        `when`(mockUploadTask.addOnSuccessListener(ArgumentMatchers.any())).thenReturn(mockStorageTask as StorageTask<UploadTask.TaskSnapshot>?)

        val mockTask = mock(Task::class.java)
        `when`(mockReference.downloadUrl).thenReturn(mockTask as Task<Uri>?)
        `when`(mockTask.addOnSuccessListener(ArgumentMatchers.any())).thenReturn(mockTask)

        FirebaseStorageService.storageProvider = { mockFirebase }
    }

    @Before
    fun before() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            fauth = FirebaseAuthenticationService()
            Auth.setAuthenticationService(fauth)

            Auth.signOut()
            Auth.isLoggedIn.value = false
            Auth.email = null
            Auth.name = null
        }
    }

    @Test
    fun getCurrentUserNullTest(){
        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.currentUser).thenReturn(null)

        fauth.setAuth(mockAuth)

        assert(fauth.getCurrentUser() == null)
    }

    @Test
    fun getCurrentUserPresentTest(){
        val user = User("fake_id", "fake_name", "fake_email", " ")
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn(user.uid)
        `when`(mockUser.email).thenReturn(user.email)
        `when`(mockUser.displayName).thenReturn(user.username)
        `when`(mockAuth.currentUser).thenReturn(mockUser)

        fauth.setAuth(mockAuth)

        assertEquals(fauth.getCurrentUser(), user)
    }

    @Test
    fun onActivityResultWrongRequestCode(){
        testRule.scenario.onActivity {
            fauth.onActivityResult(it!!, 0, 0, null){}
        }
    }

    @Test
    fun onActivityResultCorrectRequestCode(){
        testRule.scenario.onActivity {
            fauth.onActivityResult(it!!, 9001, 0, null){}
        }
    }
}