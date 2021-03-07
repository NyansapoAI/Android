package com.example.edward.nyansapo.presentation.utils

import com.example.edward.nyansapo.Instructor
import com.example.edward.nyansapo.presentation.utils.Constants.COLLECTION_ASSESSMENTS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    val studentsCollection: CollectionReference
        get() = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_ROOT + "/" + instructor_id + "/" + Constants.COLLECTION_STUDENTS)
    val assessmentsCollection: CollectionReference
        get() = studentsCollection.document(Constants.studentDocumentSnapshot!!.id).collection(COLLECTION_ASSESSMENTS)


    fun instructor(onComplete: (Instructor?) -> Unit) {

        firestoreInstance.collection(Constants.COLLECTION_ROOT).document(instructor_id).get().addOnSuccessListener {
            onComplete(it.toObject(Instructor::class.java))

        }
    }

    val instructor_id: String
        get() {
            return firebaseAuth.currentUser!!.uid
        }
    val isLoggedIn: Boolean
        get() {
            return firebaseAuth.currentUser != null
        }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.collection(Constants.COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid)


    fun getCurrentUser(onComplete: (DocumentSnapshot?) -> Unit) {
        firestoreInstance.collection(Constants.COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    onComplete(it)
                }
    }

    fun isInstructorSetUp(onComplete: (Boolean?) -> Unit) {
        firestoreInstance.collection(Constants.COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(true)

                    } else {
                        onComplete(false)

                    }
                }
    }

    fun saveInstructor(instructor: Instructor, onComplete: () -> Unit) {
        firestoreInstance.collection(Constants.COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).set(instructor).addOnSuccessListener {
            onComplete()
        }
    }

}