package com.example.edward.nyansapo.presentation.ui.attendance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.ActivityAttendanceBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.example.edward.nyansapo.registerStudent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_attendance.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.recyclerview
import java.text.SimpleDateFormat


class AttendanceFragment : Fragment(R.layout.activity_attendance) {

    private val TAG = "AttendanceFragment"

    lateinit var sharedPreferences: SharedPreferences
    lateinit var programId: String
    lateinit var groupId: String
    lateinit var campId: String

    lateinit var binding: ActivityAttendanceBinding
    lateinit var adapter: AttendanceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityAttendanceBinding.bind(view)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        setUpToolBar()
        setCurrentDate()
        getCurrentInfo()
        setOnClickListeners()

        //checking if there is student in the database
        checkIfWeHaveAnyStudentInTheGroup()

        getCurrentDateToPlaceInDatabase {
            initRecyclerViewAdapter(it)

        }


    }

    private fun setOnClickListeners() {
        binding.submitBtn.setOnClickListener {
            submitBtnClicked()
        }
    }

    private fun submitBtnClicked() {
        binding.submitBtn.isVisible=false

        //set edit item to visible
        binding.toolbar.root.menu.findItem(R.id.editItem).isVisible=true
    }

    private fun setUpToolBar() {
    binding.toolbar.root.inflateMenu(R.menu.attendance_menu)
        binding.toolbar.root.setOnMenuItemClickListener { item->

            when(item.itemId){
                R.id.editItem->
                {
                    editItemClicked()
                }


            }
            true

        }
    }

    private fun editItemClicked() {
        binding.submitBtn.isVisible=true
    }

    private fun checkIfWeHaveAnyStudentInTheGroup() {
        FirebaseUtils.getCollectionStudentFromGroup(programId, groupId).get().addOnSuccessListener {

            if (it.isEmpty) {
                Toasty.info(requireContext(), "No Student In the Database").show()
                MaterialAlertDialogBuilder(requireContext()!!).setBackground(requireActivity().getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_add_24).setTitle("Add Student").setMessage("Do you want to add student? ").setNegativeButton("no") { dialog, which -> }.setPositiveButton("yes") { dialog, which -> goToAddStudent() }.show()

            } else {
                //
            }

        }

    }

    private fun goToAddStudent() {
        val myIntent = Intent(requireContext(), registerStudent::class.java)
        startActivity(myIntent)

    }

    private fun initRecyclerViewAdapter(date: String) {
        Log.d(TAG, "initRecyclerViewAdapter: ")
        val query: Query = FirebaseUtils.getCollectionStudentFromCamp_attendance(programId, groupId, campId, date)
        val firestoreRecyclerOptions =
                FirestoreRecyclerOptions.Builder<StudentAttendance>().setQuery(query, StudentAttendance::class.java)
                        .setLifecycleOwner(viewLifecycleOwner).build()


        adapter = AttendanceAdapter(firestoreRecyclerOptions) { documentSnapshot, isChecked ->
            onCheckBoxClicked(documentSnapshot, isChecked)
        }
        recyclerview.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerview.setAdapter(adapter)

    }

    private fun onCheckBoxClicked(documentSnapshot: DocumentSnapshot, ischecked: Boolean) {
        Log.d(TAG, "onCheckBoxClicked: started updating attendance")
        val map = mapOf("present" to ischecked)
        documentSnapshot.reference.set(map, SetOptions.merge()).addOnSuccessListener {
            Log.d(TAG, "onCheckBoxClicked: success updating attendance")
        }
    }


    private fun getCurrentInfo() {
        Log.d(TAG, "getCurrentInfo: getting stuff from shared preference")
        programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(requireContext(), "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun setCurrentDate() {
        Log.d(TAG, "setCurrentDate: setting current date")
        FirebaseUtils.getCurrentDate {
            binding.dateBtn.text = SimpleDateFormat.getDateTimeInstance().format(it)


        }
    }

    private fun getCurrentDateToPlaceInDatabase(onComplete: (String) -> Unit) {
        Log.d(TAG, "setCurrentDate: setting current date")
        FirebaseUtils.getCurrentDateFormatted {

            onComplete(it!!)
        }
    }
}