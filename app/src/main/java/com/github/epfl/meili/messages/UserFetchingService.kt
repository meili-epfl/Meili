package com.github.epfl.meili.messages

import android.content.Intent
import com.github.epfl.meili.models.User.Companion.toUser
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.*
import com.github.epfl.meili.home.AuthenticationService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.github.epfl.meili.models.User
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object UserFetchingService {
    private const val TAG = "UserFetchingService"

     suspend fun getUsers(): List<User>? { // suspend makes function asynchronous
        val db = Firebase.firestore
        return try {
            db.collection("users")
                .get()
                .addOnSuccessListener { Log.d(TAG, "succes") }
                .addOnFailureListener {  Log.d(TAG, "failure")}
                .await() // wait asynchronously for data to arrive
                .documents.mapNotNull {
                    Log.d(TAG, "the user is ${it.toUser()}")
                    it.toUser() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Post from database")
            null // Return null if error occurs
        }
    }


}