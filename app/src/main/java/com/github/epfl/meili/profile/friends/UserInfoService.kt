package com.github.epfl.meili.profile.friends

import com.github.epfl.meili.database.FirestoreDocumentService
import com.github.epfl.meili.models.User

open class UserInfoService {
    companion object {
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
            onError: ((Error) -> Unit)?
    ) {
        if (onError != null && onSuccess != null) {
            if (responsesRemaining != 0) {
                onError(Error("Response is currently being processed"))
            } else {
                responsesRemaining = uids.size
                for (uid in uids) {
                    getSingleUserInfo(uid, onSuccess, onError)
                }
            }
        }
    }

    private fun getSingleUserInfo(uid: String, onSuccess: ((Map<String, User>) -> Unit), onError: ((Error) -> Unit)) {
        documentService().getDocument(getUserPath(uid)).addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject(User::class.java)
                if (user == null) {
                    onError(Error("Error fetching response"))
                    responsesRemaining = 0
                } else {
                    usersInfo[user.uid] = user
                    if (responsesRemaining == 1) {
                        onSuccess(usersInfo)
                        usersInfo.clear()
                        responsesRemaining = 0
                    } else {
                        responsesRemaining--
                    }
                }
            }
        }
    }
}