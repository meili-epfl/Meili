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
        }
    }
}