package com.example.edward.nyansapo.presentation.ui.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentCreateNewPageBinding
import com.example.edward.nyansapo.presentation.ui.main.MainActivity2
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.google.firebase.firestore.QuerySnapshot
import es.dmoral.toasty.Toasty
import java.util.*

class CreateFragment : Fragment(R.layout.fragment_create_new_page) {


    private val TAG = "CreateNewFragment"

    var programCheck = 0

    lateinit var programNames: QuerySnapshot
    lateinit var groupNames: QuerySnapshot
    lateinit var campNames: QuerySnapshot

    lateinit var binding: FragmentCreateNewPageBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateNewPageBinding.bind(view)
        initProgressBar()
        setUpTypeSpinner()
        setItemClickListener()
        setOnClickListeners()

    }

    private fun setOnClickListeners() {

        Log.d(TAG, "setOnClickListeners: started creation process")
        binding.createBtn.setOnClickListener { createStuff() }
    }

    private fun setUpTypeSpinner() {
        val spinnerValue = arrayOf("Program", "Group", "Camp")
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(MainActivity2.activityContext!!, R.layout.item_spinner_normal, spinnerValue)
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_normal_dropdown)
        binding.typeSpinner.adapter = arrayAdapter

    }

    private fun setItemClickListener() {

        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                if (++programCheck > 1) {
                    showAppropriateViews()

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }



        binding.programNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                if (++programCheck > 1) {
                    startFetchingSpecificGroup()

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }


    }

    private fun startFetchingSpecificGroup() {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

        FirebaseUtils.getGroupNamesOnce(programID) { groups ->
            groupNames = groups

            val spinnerValue = groups.map {
                "Group: ${it.toObject(Group::class.java).number}"
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(MainActivity2.activityContext!!, R.layout.item_spinner_normal, spinnerValue)
            arrayAdapter.setDropDownViewResource(R.layout.item_spinner_normal_dropdown)
            binding.groupSpinner.adapter = arrayAdapter


        }

    }

    private fun showAppropriateViews() {
        when (binding.typeSpinner.selectedItemPosition) {
            0 -> {
                programChoosen()
            }
            1 -> {
                groupChoosen()
            }
            2 -> {
                campChoosen()
            }
        }
    }

    private fun programChoosen() {
        binding.programNameTxtinputLayout.visibility = View.VISIBLE
        binding.programNameSpinner.visibility = View.GONE
        binding.groupNameTxtinputLayout.visibility = View.VISIBLE
        binding.groupSpinner.visibility = View.GONE
    }

    private fun campChoosen() {
        binding.programNameTxtinputLayout.visibility = View.GONE
        binding.programNameSpinner.visibility = View.VISIBLE
        binding.groupNameTxtinputLayout.visibility = View.GONE
        binding.groupSpinner.visibility = View.VISIBLE
        //load spinner data for program and group
        FirebaseUtils.getProgramNamesOnce { programs ->


            if (programs.isEmpty) {
                Toasty.error(MainActivity2.activityContext!!, "you must first create a program before you proceed").show()

                return@getProgramNamesOnce
            }
            programNames = programs

            val spinnerValue = programs.map {
                "Program: ${it.toObject(Program::class.java).number}"
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(MainActivity2.activityContext!!, R.layout.item_spinner_normal, spinnerValue)
            arrayAdapter.setDropDownViewResource(R.layout.item_spinner_normal_dropdown)

            binding.programNameSpinner.adapter = arrayAdapter

            if (!programs.isEmpty) {
                loadSpinnerDataForGroups()

            }
        }


    }

    private fun loadSpinnerDataForGroups() {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

        FirebaseUtils.getGroupNamesOnce(programID) { groups ->

            if (groups.isEmpty) {
                Toasty.error(MainActivity2.activityContext!!, "you must first create a group before you proceed").show()
            }
            groupNames = groups

            val spinnerValue = groups.map {
                "Group: ${it.toObject(Group::class.java).number}"
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(MainActivity2.activityContext!!, R.layout.item_spinner_normal, spinnerValue)
            arrayAdapter.setDropDownViewResource(R.layout.item_spinner_normal_dropdown)

            binding.groupSpinner.adapter = arrayAdapter


        }
    }

    private fun groupChoosen() {
        binding.programNameTxtinputLayout.visibility = View.GONE
        binding.programNameSpinner.visibility = View.VISIBLE
        binding.groupNameTxtinputLayout.visibility = View.VISIBLE
        binding.groupSpinner.visibility = View.GONE

        //load spinner data for program
        FirebaseUtils.getProgramNamesOnce { programs ->
            programNames = programs

            if (programNames.isEmpty) {
                showToast("Please create a program first")
                return@getProgramNamesOnce
            }

            val spinnerValue = programs.map {
                "Program: ${it.toObject(Program::class.java).number}"
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(MainActivity2.activityContext!!, R.layout.item_spinner_normal, spinnerValue)
            arrayAdapter.setDropDownViewResource(R.layout.item_spinner_normal_dropdown)

            binding.programNameSpinner.adapter = arrayAdapter

        }
    }


    private fun createStuff() {

        when (binding.typeSpinner.selectedItemPosition) {
            0 -> {
                Log.d(TAG, "createStuff: creating program")
                create_Program_Group_Camp()
            }
            1 -> {
                Log.d(TAG, "createStuff: creating group")
                create_Group_Camp()

            }
            2 -> {
                Log.d(TAG, "createStuff: creating camp")
                create_Camp()

            }
        }


    }

    private fun create_Group_Camp() {
        val groupNumber = binding.groupEdtTxt.editableText.toString().trim()
        val campNumber = binding.campEdtTxt.editableText.toString().trim()

        if (groupNumber.isEmpty() || campNumber.isEmpty()) {
            Toasty.error(MainActivity2.activityContext!!, "Please enter Number of groups or Camps To create").show()

        } else {
            Log.d(TAG, "create_Group_Camp: groupNumber:$groupNumber: :campNumber:$campNumber")

            val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

            startGroupCreation(groupNumber, campNumber, programID)

        }
    }

    private fun startGroupCreation(groupNumber: String, campNumber: String, programID: String) {
        Log.d(TAG, "startGroupCreation: ")
        Log.d(TAG, "startGroupCreation: groupNumber:$groupNumber campNumber:$campNumber :programId:$programID")
        FirebaseUtils.getGroupNamesOnce(programID) { groups ->
            groupNames = groups
            var start: Int
            val amount = groupNumber.toInt()
            if (!groups.isEmpty) {
                start = groups.documents[groups.size() - 1].toObject(Camp::class.java)!!.number.toInt() + 1


                val lastNumber = start + amount - 1

                for (i in start..lastNumber) {

                    val group = Group(i.toString())
                    FirebaseUtils.addGroup(programID, group) { groupID ->

                        //finished adding group in database now start adding camps
                        startCampCreation(campNumber, groupID, programID)
                    }
                }


            } else {
                for (i in 1..amount) {

                    val group = Group(i.toString())
                    FirebaseUtils.addGroup(programID, group) { groupID ->

                        //finished adding group in database now start adding camps
                        startCampCreation(campNumber, groupID, programID)
                    }
                }
            }


        }

    }

    private fun create_Program_Group_Camp() {
        Log.d(TAG, "create_Program_Group_Camp: ")
        val programName = binding.programNameEdtTxt.editableText.toString().trim()
        if (TextUtils.isEmpty(programName)) {
            Log.d(TAG, "create_Program_Group_Camp: please enter program name")
            Toasty.error(MainActivity2.activityContext!!, "Please enter Program Name ").show()
        } else {
            startProgramCreation(programName)
        }
    }

    private fun startProgramCreation(programName: String) {
        Log.d(TAG, "startProgramCreation: ")

        val groupNumber = binding.groupEdtTxt.editableText.toString().trim()
        val campNumber = binding.campEdtTxt.editableText.toString().trim()

        if (TextUtils.isEmpty(groupNumber) || TextUtils.isEmpty(campNumber)) {
            Toasty.error(MainActivity2.activityContext!!, "please enter group number or camp number").show()


        } else {
            val program = Program(programName)
            showProgress(true)
            FirebaseUtils.addProgram(program) { programID ->


                //finished adding group in database now start adding camps
                startGroupCreation(groupNumber, campNumber, programID)


            }
        }


    }


    private fun create_Camp() {
        val campNumber =
                binding.campEdtTxt.editableText.toString().trim()

        if (!TextUtils.isEmpty(campNumber)) {

            //start camp creation
            val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id
            val groupID = groupNames.documents.get(binding.groupSpinner.selectedItemPosition).id
            startCampCreation(campNumber, groupID, programID)


        } else {
            Toasty.error(MainActivity2.activityContext!!, "Please enter Number of Camps To create").show()

        }

    }

    private fun startCampCreation(campNumber: String, groupID: String, programID: String) {
        Log.d(TAG, "startCampCreation: ")
        FirebaseUtils.getCampNamesOnce(programID, groupID) { camps ->


            showProgress(false)
            (MainActivity2.activityContext as MainActivity2).supportFragmentManager.popBackStackImmediate()


            campNames = camps
            var start: Int
            val amount = campNumber.toInt()

            if (!camps.isEmpty) {
                start = camps.documents[camps.size() - 1].toObject(Camp::class.java)!!.number.toInt() + 1


                val lastNumber = start + amount

                for (i in start..lastNumber) {

                    val camp = Camp(i.toString())
                    FirebaseUtils.addCamp(programID, groupID, camp) {
                        //finished adding camp in database
                    }
                }


            } else {

                for (i in 1..amount) {

                    val camp = Camp(i.toString())
                    FirebaseUtils.addCamp(programID, groupID, camp) {
                        //finished adding camp in database
                    }
                }

            }


        }


    }


    private fun showToast(message: String) {
        Toasty.error(MainActivity2.activityContext!!, message).show()

    }

    /////////////////////PROGRESS_BAR////////////////////////////
    lateinit var dialog: AlertDialog

    private fun showProgress(show: Boolean) {

        if (show) {
            dialog.show()

        } else {
            dialog.dismiss()

        }

    }

    private fun initProgressBar() {

        dialog = setProgressDialog(MainActivity2.activityContext!!, "Loading..")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }


    //end progressbar

}