package com.example.edward.nyansapo.presentation.utils

import com.google.firebase.firestore.DocumentSnapshot

class Constants {
    companion object {
        const val SHARED_PREF_NAME: String = "shared_pref"

        //used for getting ids
        const val KEY_PROGRAM_ID = "program_id"
        const val KEY_GROUP_ID = "group_id"
        const val KEY_CAMP_ID = "camp_id"

        //used for getting positions
        const val PROGRAM_POS = "program_pos"
        const val GROUP_POS = "group_pos"
        const val CAMP_POS = "camp_pos"
    }

}

var studentDocumentSnapshot: DocumentSnapshot? = null
var assessmentDocumentSnapshot: DocumentSnapshot? = null

var answerQ1: String? = null
var answerQ2: String? = null


val COLLECTION_ROOT = "nyansapo"
val COLLECTION_STUDENTS = "students"
val COLLECTION_ASSESSMENTS = "assessments"


val STUDENT_ID = "studentId"
