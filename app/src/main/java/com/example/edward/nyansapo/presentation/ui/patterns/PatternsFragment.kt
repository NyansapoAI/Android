package com.example.edward.nyansapo.presentation.ui.patterns

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.Assessment
import com.example.edward.nyansapo.Learning_Level
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.FragmentDataAnalyticsBinding
import com.example.edward.nyansapo.presentation.ui.main.MainActivity2
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import es.dmoral.toasty.Toasty

class PatternsFragment: Fragment(R.layout.fragment_data_analytics) {
    private val TAG = "DataAnalyticsFragment"


    lateinit var binding: FragmentDataAnalyticsBinding
    var numberStudentsWhoImproved: Int = 0

    var list_letters: MutableList<Student> = mutableListOf<Student>()
    var list_words: MutableList<Student> = mutableListOf<Student>()
    var list_paragraph: MutableList<Student> = mutableListOf<Student>()
    var list_story: MutableList<Student> = mutableListOf<Student>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDataAnalyticsBinding.bind(view)
        setUpToolbar()

        getStudents { list ->
            val students = list.toObjects(Student::class.java)

            setNumberOfStudents(students.size)

            sortStudents(students)

            val graph = binding.cumulativeGraph
            val series = BarGraphSeries(arrayOf<DataPoint>(
                    DataPoint(1.toDouble(), list_letters!!.size.toDouble()),
                    DataPoint(2.toDouble(), list_words!!.size.toDouble()),
                    DataPoint(3.toDouble(), list_paragraph!!.size.toDouble()),
                    DataPoint(4.toDouble(), list_story!!.size.toDouble())
                    //DataPoint(5.toDouble(), (students as ArrayList<Student>).size.toDouble())
            ))

            //set spacing between bars
            series.spacing = 10
            series.isAnimated = true
            graph.addSeries(series)
            graph.title = "Students Vs. Literacy Level"

            graph.gridLabelRenderer.horizontalAxisTitle = "Literacy Level"
            graph.gridLabelRenderer.verticalAxisTitle = "Students"

            //graph.viewport.isScalable = true
            ///graph.viewport.isScrollable = true
            //graph.viewport.setScalableY(true)


            graph.viewport.isXAxisBoundsManual = true
            graph.viewport.setMinX(0.5)
            graph.viewport.setMaxX(4.5)
            graph.viewport.isYAxisBoundsManual = true
            graph.viewport.setMinY(0.0)
            //graph.viewport.setMaxY((students as ArrayList<Student>).size.toDouble())
            graph.viewport.setMaxY(getMaxY()) // get Maximum Y dynamically from the data
            graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    return if (isValueX) {
                        when (value.toInt()) {
                            1 -> "Letter"
                            2 -> "Word"
                            3 -> "Paragraph"
                            4 -> "Story"
                            5 -> "Total"
                            else -> "U"
                        }
                    } else super.formatLabel(value, isValueX)
                }
            }
            try {
                setMissedWords()
            } catch (e: Exception) {
                //there is possibility of user closing this screen while am still trying to get the assessment list hence the MainActivity2.activityContext!! will return null
                e.printStackTrace()
            }


        }


    }

    private fun getMaxY(): Double{
        var max1 =  kotlin.math.max(list_letters!!.size.toDouble(), list_words!!.size.toDouble())
        var max2 =  kotlin.math.max( list_paragraph!!.size.toDouble(), list_story!!.size.toDouble())
        return kotlin.math.max(max1, max2) + 1.0
    }

    private fun setUpToolbar() {
        binding.toolbar.root.inflateMenu(R.menu.overflow_menu)
        binding.toolbar.root.title = "Patterns"
    }


    private fun setNumberOfStudents(size: Int) {
        binding.totalStudentsTxtView.text = "$size \n Students"
    }

    private fun setMissedWords() {
        getAllAssessments { assessments ->
            Log.d(TAG, "setMissedWords: gotten all assessment assement size: ${assessments.size}")

            var wrongWords: StringBuilder = StringBuilder()
            for (assessment in assessments) {
                wrongWords.append(assessment.wordsWrong)
                wrongWords.append(assessment.paragraphWordsWrong)
                wrongWords.append(assessment.storyWordsWrong)
            }
            Log.d(TAG, "setMissedWords: finished appending wrong words: ${wrongWords.toString()}")

            val wordList = wrongWords.split(",", ignoreCase = true).filter {
                !it.isBlank()
            }.map {
                it.trim()
            }

            val map = mutableMapOf<String, Int>()
            for (word in wordList) {

                if (map.containsKey(word)) {
                    map[word] = map[word]!! + 1
                } else {
                    map[word] = 0
                }
            }


            map.toList().sortedByDescending { it -> it.second }.forEachIndexed { index, pair ->
                Log.d(TAG, "setMissedWords: missed word Are: $pair")

                if (index < 6) {
                    val tag = "word_$index"
                    val textview = binding.root.findViewWithTag<TextView>(tag)
                    textview.text = pair.first

                } else {
                    return@forEachIndexed
                }


            }

        }
    }

    private fun getAllAssessments(onComplete: (MutableList<Assessment>) -> Unit) {
        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }

        val assessments = mutableListOf<Assessment>()
        val taskList = mutableListOf<Task<QuerySnapshot>>()
        FirebaseUtils.getCollectionStudentFromCamp_ReturnSnapshot(programId, groupId, campId) { querySnapshot ->

            Log.d(TAG, "getAllAssessments: number of students fetched: ${querySnapshot.size()}")
            val numberStudentsWhoImproved: Int = 0
            for (snapshot in querySnapshot) {

                val task = getAssessmentFromSpicificStudent(snapshot) { specificStudentAssessments ->
                    Log.d(TAG, "getAllAssessments: for student snapshot ${snapshot.id}: has ${specificStudentAssessments.size} assessments")
                    Log.d(TAG, "getAllAssessments: assessments: $specificStudentAssessments")

                    findOutIfStudentImproved(specificStudentAssessments)

                    assessments.addAll(specificStudentAssessments)

                }

                if (task != null) {
                    taskList.add(task)

                }


            }
            Tasks.whenAll(taskList).addOnSuccessListener {
                onComplete(assessments)
            }
        }

    }

    private fun findOutIfStudentImproved(specificStudentAssessments: List<Assessment>) {
        if (specificStudentAssessments.size < 2) {
            Log.d(TAG, "findOutIfStudentImproved: no improvement since we have less than one assessment")
        } else {
            try {
                val recent = Learning_Level.valueOf(specificStudentAssessments.get(0).learningLevel)
                val past = Learning_Level.valueOf(specificStudentAssessments.get(1).learningLevel)

                if (recent.ordinal > past.ordinal) {
                    binding.improvementTxtView.text = " ${++numberStudentsWhoImproved}   improved by \n one literacy level"
                }
            } catch (e: java.lang.Exception) {
                // handler
            } finally {
                // optional finally block
            }

        }
    }

    private fun getAssessmentFromSpicificStudent(snapshot: QueryDocumentSnapshot, onComplete: (List<Assessment>) -> Unit): Task<QuerySnapshot>? {


        if (context != null) {
            val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
            val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
            val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
            val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

            if (campPos == -1) {
                Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
                MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
            }

            val task = FirebaseUtils.getAssessmentsFromStudent_Task(programId, groupId, campId, snapshot.id)

            task.addOnSuccessListener {
                onComplete(it.toObjects(Assessment::class.java))
            }
            return task

        } else {
            return null
        }


    }

    fun sortStudents(students: List<Student>?) {
        val len = students!!.size
        for (i in 0 until len) {
            when (students[i].learningLevel) {
                "LETTER" -> {
                    list_letters!!.add(students[i])
                }
                "WORD" -> {
                    list_words!!.add(students[i])
                }
                "PARAGRAPH" -> {
                    list_paragraph!!.add(students[i])
                }
                "STORY" -> {
                    list_story!!.add(students[i])
                }
            }
        }
    }

    private fun getStudents(onComplete: (QuerySnapshot) -> Unit) {
        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }

        FirebaseUtils.getCollectionStudentFromCamp_ReturnSnapshot(programId, groupId, campId) {
            onComplete(it)
        }

    }

}