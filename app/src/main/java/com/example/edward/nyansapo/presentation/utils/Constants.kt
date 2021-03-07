package com.example.edward.nyansapo.presentation.utils

import com.google.firebase.firestore.DocumentSnapshot

object Constants {

    var studentDocumentSnapshot: DocumentSnapshot?=null
    var assessmentDocumentSnapshot: DocumentSnapshot?=null


    val COLLECTION_ROOT="nyansapo"
    val COLLECTION_STUDENTS="students"
    val COLLECTION_ASSESSMENTS="assessments"
}