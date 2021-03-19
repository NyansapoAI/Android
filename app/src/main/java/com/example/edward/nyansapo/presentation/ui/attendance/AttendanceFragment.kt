package com.example.edward.nyansapo.presentation.ui.attendance

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.ActivityAttendanceBinding
import com.example.edward.nyansapo.databinding.ItemAttendanceBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.example.edward.nyansapo.registerStudent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*


class AttendanceFragment : Fragment(R.layout.activity_attendance) {

    private val TAG = "AttendanceFragment"

    lateinit var sharedPreferences: SharedPreferences
    lateinit var programId: String
    lateinit var groupId: String
    lateinit var campId: String

    lateinit var binding: ActivityAttendanceBinding
    lateinit var adapter: AttendanceAdapter
    lateinit var currentDateServer: Date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityAttendanceBinding.bind(view)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        setUpToolBar()
        setCurrentDate()
        getCurrentInfo()
        setOnClickListeners()

        initDataFetching()


    }

    private fun initDataFetching(datePickerDate: String? = null) {

        //checking if there is student in the database
        checkIfWeHaveAnyStudentInTheCamp() { databaseIsEmpty, querySnapshot ->

            //are we using date from the date picker or date from server
            if (datePickerDate == null) {
                Log.d(TAG, "initDataFetching: we using date from server")
                getCurrentDateToPlaceInDatabase { date ->

                    if (!databaseIsEmpty) {
                        initRecyclerViewAdapter(date)
                        setSwipeListenerForItems()
                        startFetchingStudentFromCampAndPlaceThem_InAttendanceThatIsIfTheAttendanceIsEmpty(date, querySnapshot)

                    }

                }
            } else {
                Log.d(TAG, "initDataFetching: we are using date from datepicker")
                if (!databaseIsEmpty) {
                    initRecyclerViewAdapter(datePickerDate)
                    startFetchingStudentFromCampAndPlaceThem_InAttendanceThatIsIfTheAttendanceIsEmpty(datePickerDate, querySnapshot)

                }

            }


        }
    }

    private fun setOnClickListeners() {

        binding.dateBtn.setOnClickListener {
            dateBtnClicked()
        }

        binding.submitBtn.setOnClickListener {
            submitBtnClicked()
        }
    }

    private fun dateBtnClicked() {
        val myCalendar = Calendar.getInstance()
        myCalendar.time = currentDateServer

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            //month usually starts from 0
            updateLabel(dayOfMonth, monthOfYear + 1, year)
        }


        DatePickerDialog(requireContext(), dateSetListener, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]).show()


    }

    private fun updateLabel(dayOfMonth: Int, monthOfYear: Int, year: Int) {
        val data = "$dayOfMonth" + "/" + "${monthOfYear}" + "/" + "$year"
        Log.d(TAG, "updateLabel: ${data}")
//check if we have choosen a future date and reject it if its future date

        val myCalendar = Calendar.getInstance()
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear - 1);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        val choosenDate = myCalendar.time

///checks if we are on same day
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = choosenDate
        cal2.time = currentDateServer

        val sameDay = cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR] &&
                cal1[Calendar.YEAR] == cal2[Calendar.YEAR]

        if (sameDay) {
            //nothing to do hear
        } else if (choosenDate.after(currentDateServer)) {

            myCalendar.get(Calendar.YEAR)

            Toasty.error(requireContext(), "Please Don't Choose  Future date only past  can be choosen").show()
            return
        }

        binding.dateBtn.text = data
        var dateFormated = data.replace("/", "_")
        dateFormated = dateFormated.replace("0", "")

        //start fetching data again with new date from picker
        initDataFetching(dateFormated)

    }

    private fun submitBtnClicked() {
        if (this::adapter.isInitialized) {
            binding.submitBtn.isVisible = false

            //set edit item to visible
            binding.toolbar.root.menu.findItem(R.id.editItem).isVisible = true

            weWantToChangeIfTheCheckBoxIsEnabledOrDisabledInTheRecylerView(false)

        } else {
            Log.d(TAG, "submitBtnClicked: adapter not initialized maybe becos database is empty")
            Toasty.info(requireContext(), "No Data In Database").show()
        }

    }

    private fun weWantToChangeIfTheCheckBoxIsEnabledOrDisabledInTheRecylerView(enabled: Boolean) {
        if (adapter != null) {
            Log.d(TAG, "weWantToChangeIfTheCheckBoxIsEnabledOrDisabledInTheRecylerView: started updading checkboxes")
            for (position in 0 until binding.recyclerview.adapter!!.itemCount) {
                Log.d(TAG, "weWantToChangeIfTheCheckBoxIsEnabledOrDisabledInTheRecylerView: $position updated")
                val binding = ItemAttendanceBinding.bind(binding.recyclerview.getChildAt(position))
                binding.attendanceCheckbox.isEnabled = enabled
                adapter.notifyDataSetChanged()


            }
        } else {
            Toasty.info(requireContext(), "Adapter is null").show()
            Log.d(TAG, "recreateAdapterWithCheckBoxesDisabled: adapter is null")
        }

    }

    private fun setUpToolBar() {
        binding.toolbar.root.inflateMenu(R.menu.attendance_menu)
        binding.toolbar.root.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.editItem -> {
                    editItemClicked(item)
                }


            }
            true

        }
    }

    private fun editItemClicked(item: MenuItem) {
        binding.submitBtn.isVisible = true
        item.isVisible = false
        weWantToChangeIfTheCheckBoxIsEnabledOrDisabledInTheRecylerView(true)
    }

    private fun checkIfWeHaveAnyStudentInTheCamp(onComplete: (Boolean, QuerySnapshot) -> Unit) {
        FirebaseUtils.getCollectionStudentFromCamp_ReturnSnapshot(programId, groupId, campId) {

            if (it.isEmpty) {
                Log.d(TAG, "checkIfWeHaveAnyStudentInTheGroup: not student in database")
                Toasty.info(requireContext(), "No Student In the Database").show()
                MaterialAlertDialogBuilder(requireContext()!!).setBackground(requireActivity().getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_add_24).setTitle("Add Student").setMessage("Database is Empty,Do you want to add student? ").setNegativeButton("no") { dialog, which -> }.setPositiveButton("yes") { dialog, which -> goToAddStudent() }.show()

            }

            onComplete(it.isEmpty, it)
        }

    }

    private fun startFetchingStudentFromCampAndPlaceThem_InAttendanceThatIsIfTheAttendanceIsEmpty(date: String, querySnapshot: QuerySnapshot) {
        FirebaseUtils.getCollectionStudentFromCamp_attendance_ReturnSnapshot(programId, groupId, campId, date) {

            if (it.isEmpty) {
                addStudentsToAttendance(date, querySnapshot)
            }

        }

    }

    private fun addStudentsToAttendance(date: String, querySnapshot: QuerySnapshot) {
        Log.d(TAG, "addStudentsToAttendance: Started adding students")

        for (documentSnapshot in querySnapshot) {
            val student = documentSnapshot.toObject(Student::class.java)

            val studentAttendance = StudentAttendance(student.firstname + " " + student.lastname)

            FirebaseUtils.addStudentsToAttendance(programId, groupId, campId, documentSnapshot.id, date, studentAttendance) {
                Log.d(TAG, "addStudentsToAttendance: success adding student")
            }


        }


    }

    private fun goToAddStudent() {
        val myIntent = Intent(requireContext(), registerStudent::class.java)
        startActivity(myIntent)

    }

    private fun initRecyclerViewAdapter(date: String) {
        Log.d(TAG, "initRecyclerViewAdapter: ")
        val query: Query = FirebaseUtils.getCollectionStudentFromCamp_attendance_ReturnCollection(programId, groupId, campId, date)
        val firestoreRecyclerOptions =
                FirestoreRecyclerOptions.Builder<StudentAttendance>().setQuery(query, StudentAttendance::class.java)
                        .setLifecycleOwner(viewLifecycleOwner).build()


        adapter = AttendanceAdapter(firestoreRecyclerOptions) { documentSnapshot, isChecked ->
            onCheckBoxClicked(documentSnapshot, isChecked)
        }
        recyclerview.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerview.setAdapter(adapter)

    }

    private fun setSwipeListenerForItems() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteFromDatabase(viewHolder.bindingAdapterPosition)
            }
        }).attachToRecyclerView(recyclerview)
    }

    private fun deleteFromDatabase(position: Int) {
        adapter.snapshots.getSnapshot(position).reference.delete().addOnSuccessListener {
            Log.d(TAG, "deleteFromDatabase: success deleting attendance")
        }
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

            currentDateServer = it!!

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