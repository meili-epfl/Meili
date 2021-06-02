package com.github.epfl.meili.profile.friends

import android.util.Log
import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.models.User

open class UserInfoService {
    companion object {
        private const val TAG = "UserInfoService"

        var documentService: () -> FirestoreDocumentService = { FirestoreDocumentService }
        var getUserPath: (String) -> String = { uid -> "users/${uid}" }
    }

    private var usersInfo: HashMap<String, User> = HashMap()
    private var responsesRemaining = 0

    /**
     * @param uids list of uids that we want to user information of
     * @param onSuccess function that will be called with the mapping between uid and User object
     * if operation successful
     * @param onError function that will be called if any error happens
     *
     * This function will fetch one-by-one the user information from the Firestore and will call
     * onSuccess with the result if successful otherwise it will call onError
     */
    open fun getUserInformation(
            uids: List<String>,
            onSuccess: ((Map<String, User>) -> Unit)?,
    ) {
        if (onSuccess != null) {
            if (responsesRemaining != 0) {
                Log.e(TAG, "Error fetching user information:")
            } else {
                if (uids.isEmpty()) {
                    onSuccess(HashMap())
                }

                val uidsSet = uids.toSet()
                responsesRemaining = uidsSet.size

                for (uid in uidsSet) {
                    getSingleUserInfo(uid, onSuccess)
                }
            }
        }
    }

    private fun getSingleUserInfo(uid: String, onSuccess: ((Map<String, User>) -> Unit)) {
        documentService().getDocument(getUserPath(uid)).addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject(User::class.java)

                if (user != null) {
                    usersInfo[user.uid] = user
                }

                checkIfDone(onSuccess)
            } else {
                checkIfDone(onSuccess)
            }
        }
    }

    private fun checkIfDone(onSuccess: ((Map<String, User>) -> Unit)) {
        if (responsesRemaining == 1) {
            onSuccess(usersInfo)
            usersInfo = HashMap()
            responsesRemaining = 0
        } else {
            Log.d("getSingleUserInfo", responsesRemaining.toString())
            responsesRemaining--
        }
    }
}