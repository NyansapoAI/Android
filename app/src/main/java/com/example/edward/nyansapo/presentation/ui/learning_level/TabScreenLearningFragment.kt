package com.example.edward.nyansapo.presentation.ui.learning_level

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edward.nyansapo.*
import com.example.edward.nyansapo.AddDialog
import com.example.edward.nyansapo.databinding.FragmentTabScreenLearningBinding

class TabScreenLearningFragment : Fragment(R.layout.fragment_tab_screen_learning) {
      companion object {
              private  const val TAG="TabScreenLearningFragme"
          }

    lateinit var binding: FragmentTabScreenLearningBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        binding = FragmentTabScreenLearningBinding.bind(view)

        setOnClickListeners()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {


   /*     val adapter = LearningAdapter()
        adapter.submitList(getStudents())    // populate students ArrayList


        binding.recyclerview.apply {
            setAdapter(adapter)
            setLayoutManager(LinearLayoutManager(requireContext()))

        }*/

    }

    private fun setOnClickListeners() {
        binding.fab.setOnClickListener {
            addstudent()
        }
    }

    fun addstudent() {

        val databaseHander = dataBaseHandler(requireContext())

        val instructor_id = "123"

        val myIntent = Intent(requireContext(), registerStudent::class.java)
        myIntent.putExtra("instructor_id", instructor_id)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
    }


    fun getStudents():List<Student> {
        val databaseHander = dataBaseHandler(requireContext())

        var instructor_id = "123"

        if (instructor_id == null || instructor_id === "") {
            instructor_id = "5f39b701b4270100524952ed"
        }

        //students = databasehelper.getAllStudent();
       val students= databaseHander.getAllStudentOfInstructor(instructor_id) as List<Student>
        if (students.size == 0) {
            // create an info alert
            openDialog()
        }
        return students
    }

    fun openDialog() {
        val addDialog = AddDialog()
        addDialog.setInfo("Add Student", "Do you want to add a student?")
        addDialog.show(requireActivity().supportFragmentManager, "Add student")
    }

}