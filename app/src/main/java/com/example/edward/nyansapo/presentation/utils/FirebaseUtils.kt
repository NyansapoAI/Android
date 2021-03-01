package com.example.edward.nyansapo.presentation.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {

     val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
     val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.collection(Constants.COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid)


    fun getCurrentUser(onComplete: (DocumentSnapshot?) -> Unit) {
        firestoreInstance.collection(Constants.COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    onComplete(it)
                }
    }


}