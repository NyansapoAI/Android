package com.example.edward.nyansapo.presentation.ui.assessment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.Assessment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.SelectAssessment
import com.example.edward.nyansapo.databinding.ActivityBeginAssessementBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import es.dmoral.toasty.Toasty
import java.util.*

class BeginAssessmentFragment : Fragment(R.layout.activity_begin_assessement) {


    private val TAG = "BeginAssessmentFragment"

    lateinit var assessmentList: List<Assessment>

    lateinit var binding: ActivityBeginAssessementBinding
    lateinit var studentId: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityBeginAssessementBinding.bind(view)
        studentId = studentDocumentSnapshot!!.id
        setOnClickListeners()
        checkIfDatabaseIsEmpty()

    }

    private fun setOnClickListeners() {

        binding.beginAssessmentBtn.setOnClickListener {
            addAssessment()

        }
    }

    private fun addAssessment() {

        Log.d(TAG, "addAssessment: btn clicked ")
        val intent = Intent(requireContext(), SelectAssessment::class.java)
        startActivity(intent)
    }

    private fun checkIfDatabaseIsEmpty() {
        Log.d(TAG, "checkIfDatabaseIsEmpty: ")
        val sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(requireActivity(), "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        FirebaseUtils.getAssessmentsFromStudent(programId, groupId, campId, studentDocumentSnapshot!!.id) {
            if (it.isEmpty) {
                Log.d(TAG, "checkIfDatabaseIsEmpty: no assessments")

            } else {
                Log.d(TAG, "checkIfDatabaseIsEmpty: ${it.size()} assessments")
                //this list is need by graphview
                assessmentList = it.toObjects(Assessment::class.java) as ArrayList<Assessment>

                setUpGraph()
            }


        }
    }


    private fun setUpGraph() {
        Log.d(TAG, "setUpGraph: ${assessmentList.size}")
        assessmentList.forEach{
            Log.d(TAG, "setUpGraph: $it")
        }
        


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


}