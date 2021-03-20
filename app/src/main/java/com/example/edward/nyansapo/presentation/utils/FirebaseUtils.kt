package com.example.edward.nyansapo.presentation.utils

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import com.example.edward.nyansapo.*
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.presentation.ui.attendance.CurrentDate
import com.example.edward.nyansapo.presentation.ui.attendance.StudentAttendance
import com.example.edward.nyansapo.presentation.ui.home.Camp
import com.example.edward.nyansapo.presentation.ui.home.Group
import com.example.edward.nyansapo.presentation.ui.home.Program
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.text.SimpleDateFormat
import java.util.*

object FirebaseUtils {


    private val TAG = "FirebaseUtils"

    val COLLECTION_PROGRAM_NAMES = "program_names"
    val COLLECTION_GROUPS = "groups"
    val COLLECTION_CAMPS = "camps"
    val COLLECTION_ATTENDANCE = "attendance"

    val ORDER_BY = "number"


    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    fun getProgramNamesContinuously(onComplete: (QuerySnapshot) -> Unit): ListenerRegistration {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).orderBy(ORDER_BY).addSnapshotListener { query, e ->

            onComplete(query!!)
        }


    }

    fun getProgramNamesOnce(onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).orderBy(ORDER_BY).get().addOnSuccessListener {


            onComplete(it)
        }


    }

    fun addProgram(program: Program, onComplete: (String) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).add(program).addOnSuccessListener {

            onComplete(it.id)

        }
    }


    fun getGroupNamesContinously(programId: String, onComplete: (QuerySnapshot) -> Unit): ListenerRegistration {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).orderBy(ORDER_BY).addSnapshotListener { query, e ->
            onComplete(query!!)

        }


    }

    fun getGroupNamesOnce(programId: String, onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).orderBy(ORDER_BY).get().addOnSuccessListener {

            onComplete(it!!)

        }


    }

    fun addGroup(programId: String, group: Group, onComplete: (String) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).add(group).addOnSuccessListener {
            onComplete(it.id)
        }


    }

    fun getCampNamesContinously(programId: String, groupId: String, onComplete: (QuerySnapshot) -> Unit): ListenerRegistration {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).orderBy(ORDER_BY).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            onComplete(querySnapshot!!)

        }


    }

    fun getCampNamesOnce(programId: String, groupId: String, onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).orderBy(ORDER_BY).get().addOnSuccessListener {

            onComplete(it)

        }


    }

    fun addCamp(programId: String, groupId: String, camp: Camp, onComplete: () -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).add(camp).addOnSuccessListener {
            onComplete()
        }

    }

    ////////////////////////////
   /* val studentsCollection: CollectionReference
        get() = FirebaseFirestore.getInstance().collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_STUDENTS)
*/
  /*  fun assessmentsCollection(id: String): CollectionReference {
        ..
        return studentsCollection.document(id).collection(COLLECTION_ASSESSMENTS)
    }
*/

    fun instructor(onComplete: (Instructor?) -> Unit) {

        firestoreInstance.collection(COLLECTION_ROOT).document(instructor_id).get().addOnSuccessListener {
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
        get() = firestoreInstance.collection(COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid)


    fun getCurrentUser(onComplete: (DocumentSnapshot?) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    onComplete(it)
                }
    }

    fun isInstructorSetUp(onComplete: (Boolean?) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(true)

                    } else {
                        onComplete(false)

                    }
                }
    }

    fun saveInstructor(instructor: Instructor, onComplete: () -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT).document(firebaseAuth.currentUser!!.uid).set(instructor).addOnSuccessListener {
            onComplete()
        }
    }


    fun getCurrentDateFormatted(onComplete: (String?) -> Unit) {

        FirebaseFirestore.getInstance().collection("dummy").document("date").set(CurrentDate()).addOnSuccessListener {


            FirebaseFirestore.getInstance().collection("dummy").document("date").get().addOnSuccessListener {


                val date = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(it.toObject(CurrentDate::class.java)?.date)


                Log.d(TAG, "getCurrentDateAndInitCurrentInfo: retrieving current date from database ${date}")

                //this symbols act weird with database
                var currentDate: String
                currentDate = date.replace("/", "_")
                currentDate = currentDate.replace("0", "")

                onComplete(currentDate)

            }
        }


    }

    fun getCurrentDate(onComplete: (Date?) -> Unit) {
        FirebaseFirestore.getInstance().collection("dummy").document("date").set(CurrentDate()).addOnSuccessListener {


            FirebaseFirestore.getInstance().collection("dummy").document("date").get().addOnSuccessListener {

                onComplete(it.toObject(CurrentDate::class.java)?.date)

            }
        }


    }

    fun getCollectionStudentFromGroup_ReturnCollection(programId: String, groupId: String): CollectionReference {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_STUDENTS)
    }

    fun getCollectionStudentFromGroup_ReturnSnapshot(programId: String, groupId: String, onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_STUDENTS).get().addOnSuccessListener {
            onComplete(it)
        }
    }

    fun getCollectionStudentFromCamp_ReturnCollection(programId: String, groupId: String, campId: String): CollectionReference {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS)
    }

    fun getCollectionStudentFromCamp_ReturnSnapshot(programId: String, groupId: String, campId: String, onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS).get().addOnSuccessListener {
            onComplete(it)
        }
    }


    fun getCollectionStudentFromCamp_attendance_ReturnCollection(programId: String, groupId: String, campId: String, date: String): CollectionReference {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_ATTENDANCE).document(date).collection(COLLECTION_STUDENTS)
    }

    fun addStudentsToAttendance(programId: String, groupId: String, campId: String, studentId: String, date: String, studentAttendance: StudentAttendance, onComplete: () -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_ATTENDANCE).document(date).collection(COLLECTION_STUDENTS).document(studentId).set(studentAttendance).addOnSuccessListener {
            onComplete()
        }
    }

    fun getCollectionStudentFromCamp_attendance_ReturnSnapshot(programId: String, groupId: String, campId: String, date: String, onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_ATTENDANCE).document(date).collection(COLLECTION_STUDENTS).get().addOnSuccessListener {
            onComplete(it)
        }


    }

    fun addStudentsToCamp(programId: String, groupId: String, campId: String, student: Student, onComplete: (DocumentReference) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS).add(student).addOnSuccessListener {
            onComplete(it)
        }
    }


    fun getAssessmentsFromStudent(programId: String, groupId: String, campId: String, studentId: String, onComplete: (QuerySnapshot) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS).document(studentId).collection(COLLECTION_ASSESSMENTS).orderBy("timestamp").get().addOnSuccessListener {
            //deleting assessments that dont have a learning level
            deleteAllUnknownAssessments(it)


            onComplete(it)
        }
    }

    fun getAssessmentsFromStudent_Task(programId: String, groupId: String, campId: String, studentId: String): Task<QuerySnapshot> {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS).document(studentId).collection(COLLECTION_ASSESSMENTS).orderBy("timestamp").get()

    }

    private fun deleteAllUnknownAssessments(snapshots: QuerySnapshot) {
        for (snapshot in snapshots) {
            val assessment = snapshot.toObject(Assessment::class.java)
            if (assessment.learningLevel.equals(Learning_Level.UNKNOWN.name) || assessment.learningLevel.trim().isBlank()) {
                snapshot.reference.delete().addOnSuccessListener {
                    Log.d(TAG, "deleteAllUnknownAssessments: ")
                }
            }
        }
    }

    fun getAssessmentsFromStudent_Collection(programId: String, groupId: String, campId: String, studentId: String): CollectionReference {
        return firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS).document(studentId).collection(COLLECTION_ASSESSMENTS)


    }

    fun addAssessmentForStudent(programId: String, groupId: String, campId: String, studentId: String, assessment: Assessment, onComplete: (DocumentReference) -> Unit) {
        firestoreInstance.collection(COLLECTION_ROOT + "/" + instructor_id + "/" + COLLECTION_PROGRAM_NAMES).document(programId).collection(COLLECTION_GROUPS).document(groupId).collection(COLLECTION_CAMPS).document(campId).collection(COLLECTION_STUDENTS).document(studentId).collection(COLLECTION_ASSESSMENTS).add(assessment).addOnSuccessListener {
            onComplete(it)
        }


    }


    fun showAlertDialog(context: Context, @DrawableRes icon: Int, title: String, message: String, onYes: () -> Unit, onNo: () -> Unit) {
        MaterialAlertDialogBuilder(context).setBackground(context.getDrawable(R.drawable.button_first)).setIcon(icon).setTitle(title).setMessage(message).setNegativeButton("no") { dialog, which -> onNo() }.setPositiveButton("yes") { dialog, which -> onYes() }.show()

    }
}