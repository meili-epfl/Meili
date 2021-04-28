package com.github.epfl.meili.database

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreDocumentService {
    var databaseProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }

    fun getDocument(path: String): Task<DocumentSnapshot> {
        return databaseProvider().document(path).get()
    }

    fun setDocument(path: String, data: Any) {
        databaseProvider().document(path).set(data)
    }
}