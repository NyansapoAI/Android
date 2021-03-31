package com.nyansapo.presentation.ui.student

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nyansapo.*
import com.nyansapo.databinding.ActivityStudentInfoPageBinding
import com.nyansapo.presentation.ui.main.MainActivity2
import com.nyansapo.presentation.utils.Constants
import com.nyansapo.presentation.utils.FirebaseUtils
import com.nyansapo.presentation.utils.studentDocumentSnapshot
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class StudentAssessmentListFragment : Fragment(R.layout.activity_student_info_page) {

private val TAG = "StudentInfoPageFragment"

    lateinit var binding: ActivityStudentInfoPageBinding
    lateinit var adapter: StudentAssessmentAdapter
    lateinit var student: Student
    lateinit var assessmentList: List<Assessment>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityStudentInfoPageBinding.bind(view)
        student = studentDocumentSnapshot!!.toObject(Student::class.java)!!

        setUpToolbar()




        checkIfDatabaseIsEmpty()
        initRecyclerViewAdapter()
        setSwipeListenerForItems()

    }

    fun getLevelIndex(level: String?): Int {
        return when (level) {
            "LETTER" -> 0
            "WORD" -> 1
            "PARAGRAPH" -> 2
            "STORY" -> 3
            "ABOVE" -> 4
            else -> -1
        }
    }

    private fun setUpGraph() {


        if (assessmentList!!.size > 0) {
            //Toast.makeText(this, assessmentList.get(assessmentList.size()-1).getLEARNING_LEVEL(),Toast.LENGTH_LONG).show();
            val series = LineGraphSeries<DataPoint>()
            val num = assessmentList!!.size
            var i = 0
            while (i < num && i < 5) {
                series.appendData(DataPoint((i + 1).toDouble(), getLevelIndex(assessmentList!!.get(i).learningLevel).toDouble()), true, 5)
                i++
            }
            series.setAnimated(true)

            binding.graphview.apply {
                addSeries(series)
                title = "Literacy Level Vs. Time of Current Assessments"

                viewport.isScalable = true
                viewport.isScrollable = true
                      viewport.setScalableY(true)
                      gridLabelRenderer.horizontalAxisTitle="Time of Current Assessments"
                      gridLabelRenderer.verticalAxisTitle="Literacy Level"

                viewport.isXAxisBoundsManual = true
                viewport.setMinX(1.0)
                viewport.setMaxX(5.0)
                viewport.isYAxisBoundsManual = true
                viewport.setMinY(0.0)
                viewport.setMaxY(4.0)
                gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                    override fun formatLabel(value: Double, isValueX: Boolean): String {
                        return if (!isValueX) {
                            when (value.toInt()) {
                                0 -> "L"
                                1 -> "W"
                                2 -> "P"
                                3 -> "S"
                                4 -> "A"
                                else -> "U"
                            }
                        } else super.formatLabel(value, isValueX)
                    }
                }
            }

        } else {

            val series = LineGraphSeries(arrayOf<DataPoint>(
                    DataPoint(0.toDouble(), 0.toDouble()),
                    DataPoint(1.toDouble(), 0.toDouble()),
                    DataPoint(2.toDouble(), 0.toDouble()),
                    DataPoint(3.toDouble(), 0.toDouble()),
                    DataPoint(4.toDouble(), 0.toDouble())))
            series.setAnimated(true) // set animation
            binding.graphview.apply {
                addSeries(series)
                title = "No Assessment has been recorded"
                viewport.isXAxisBoundsManual = true
                viewport.setMinX(1.0)
                viewport.setMaxX(5.0)
                viewport.isYAxisBoundsManual = true
                viewport.setMinY(0.0)
                viewport.setMaxY(4.0)
                gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                    override fun formatLabel(value: Double, isValueX: Boolean): String {
                        return if (!isValueX) {
                            when (value.toInt()) {
                                0 -> "L"
                                1 -> "W"
                                2 -> "P"
                                3 -> "S"
                                4 -> "A"
                                else -> "U"
                            }
                        } else super.formatLabel(value, isValueX)
                    }
                }


            }

        }
    }


    private fun initRecyclerViewAdapter() {

        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }


        val query: Query = FirebaseUtils.getAssessmentsFromStudent_Collection(programId, groupId, campId, studentDocumentSnapshot!!.id).orderBy("timestamp", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Assessment>().setQuery(query, Assessment::class.java)
                .setLifecycleOwner(this).build()


        adapter = StudentAssessmentAdapter(MainActivity2.activityContext!!, firestoreRecyclerOptions) {
            onAssmentClicked(it)
        }
        recyclerview.setLayoutManager(LinearLayoutManager(MainActivity2.activityContext!!))
        recyclerview.setAdapter(adapter)

    }


    private fun setSwipeListenerForItems() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter?.deleteFromDatabase(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerview)
    }

    fun onAssmentClicked(assessment: Assessment) {
        Log.d(TAG, "onAssmentClicked: $assessment")

        MainActivity2.activityContext!!.supportFragmentManager.beginTransaction().replace(R.id.container, AssessmentResultsFragment()).addToBackStack(null).commit()

        /*  val intent = Intent(MainActivity2.activityContext!!, assessment_detail::class.java)
          intent.putExtra("assessment", assessment)
          startActivity(intent)*/
    }

    private fun checkIfDatabaseIsEmpty() {
        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }

        FirebaseUtils.getAssessmentsFromStudent(programId, groupId, campId, studentDocumentSnapshot!!.id) {
            if (it.isEmpty) {
                //  openDialog()
                //no assessments available
            } else {
                //this list is need by graphview
                assessmentList = it.toObjects(Assessment::class.java) as ArrayList<Assessment>

                setUpGraph()
            }


        }
    }

    fun openDialog() {

        FirebaseUtils.showAlertDialog(MainActivity2.activityContext!!, R.drawable.button_first, "Add Assessment", "Do you want to add an Assessment?", {
            addAssessment()
        }) {
            //no clicked

        }

       }

    private fun addAssessment() {

        Log.d(TAG, "addAssessment: btn clicked ")
        val intent = Intent(MainActivity2.activityContext!!, SelectAssessment::class.java)
        startActivity(intent)
    }

    private fun setUpToolbar() {
        binding.toolbar.root.inflateMenu(R.menu.student_menu)

        //setting up name of students
        val fullname = "${studentDocumentSnapshot!!.toObject(Student::class.java)!!.firstname}  ${studentDocumentSnapshot!!.toObject(Student::class.java)!!.lastname}"
        binding.toolbar.root.title = fullname

        binding.toolbar.root.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.settings -> {
                    val myIntent = Intent(MainActivity2.activityContext!!, studentSettings::class.java)
                    startActivity(myIntent)
                    true
                }
                R.id.add_assessment -> {
                    addAssessment()
                    true
                }
                R.id.analytics -> {
                    val intent = Intent(MainActivity2.activityContext!!, studentDetails::class.java)
                    startActivity(intent)
                    true
                }

            }



            true
        }
    }
}