package com.example.edward.nyansapo.presentation.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentCreateNewPageBinding
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.google.firebase.firestore.QuerySnapshot
import es.dmoral.toasty.Toasty

class CreateNewPageFragment:Fragment(R.layout.fragment_create_new_page) {
    var programCheck = 0

    lateinit var programNames: QuerySnapshot
    lateinit var groupNames: QuerySnapshot
    lateinit var campNames: QuerySnapshot

    lateinit var binding: FragmentCreateNewPageBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateNewPageBinding.bind(view)
        setUpTypeSpinner()
        setItemClickListener()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.createBtn.setOnClickListener { createGroup() }
    }

    private fun setUpTypeSpinner() {
        val spinnerValue = arrayOf("Program", "Group", "Camp")
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
        binding.typeSpinner.setAdapter(arrayAdapter)

    }

    private fun setItemClickListener() {

        binding.typeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++programCheck > 1) {
                    showAppropriateViews()

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })



        binding.programNameSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++programCheck > 1) {
                    startFetchingSpecificGroup()

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })


    }

    private fun startFetchingSpecificGroup() {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

        FirebaseUtils.getGroupNames(programID) { groups ->
            groupNames = groups

            val spinnerValue = groups.map {
                it.toObject(Group::class.java).name
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
            binding.groupSpinner.setAdapter(arrayAdapter)


        }

    }

    private fun showAppropriateViews() {
        when (binding.typeSpinner.selectedItemPosition) {
            1 -> {
                groupChoosen()
            }
            2 -> {
                campChoosen()
            }
        }
    }

    private fun campChoosen() {
        binding.programNameEdtTxt.visibility = View.GONE
        binding.programNameSpinner.visibility = View.VISIBLE
        binding.groupEdtTxt.visibility = View.GONE
        binding.groupSpinner.visibility = View.VISIBLE

        //load spinner data for program and group
        FirebaseUtils.getProgramNames { programs ->


            if (programs.isEmpty) {
                Toasty.error(requireContext(), "you must first create a program before you proceed").show()
            }
            programNames = programs

            val spinnerValue = programs.map {
                it.toObject(Program::class.java).name
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
            binding.programNameSpinner.setAdapter(arrayAdapter)

            if (!programs.isEmpty) {
                loadSpinnerDataForGroups()

            }
        }


    }

    private fun loadSpinnerDataForGroups() {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

        FirebaseUtils.getGroupNames(programID) { groups ->

            if (groups.isEmpty) {
                Toasty.error(requireContext(), "you must first create a program before you proceed").show()
            }
            groupNames = groups

            val spinnerValue = groups.map {
                it.toObject(Group::class.java).name
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
            binding.groupSpinner.setAdapter(arrayAdapter)


        }
    }

    private fun groupChoosen() {
        binding.programNameEdtTxt.visibility = View.GONE
        binding.programNameSpinner.visibility = View.VISIBLE

        //load spinner data for program
        FirebaseUtils.getProgramNames { programs ->
            programNames = programs

            val spinnerValue = programs.map {
                it.toObject(Program::class.java).name
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
            binding.programNameSpinner.setAdapter(arrayAdapter)

        }
    }


    private fun createGroup() {

        when (binding.typeSpinner.selectedItemPosition) {
            0 -> {
                create_Program_Group_Camp()
            }
            1 -> {
                create_Group_Camp()

            }
            2 -> {
                create_Camp()

            }
        }


    }

    private fun create_Group_Camp() {
        val groupNumber = binding.groupEdtTxt.editableText.toString().trim()
        val campNumber = binding.campEdtTxt.editableText.toString().trim()

        if (TextUtils.isDigitsOnly(groupNumber) || TextUtils.isDigitsOnly(campNumber)) {
            Toasty.error(requireContext(), "Please enter Number of groups or Camps To create").show()
        } else {

            //start group creation
            val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

            startGroupCreation(groupNumber, campNumber, programID)


            //    startCampCreation(campNumber)


        }
    }

    private fun startGroupCreation(groupNumber: String, campNumber: String, programID: String) {
        FirebaseUtils.getGroupNames(programID) { groups ->
            groupNames = groups
            var start: Int
            val amount = groupNumber.toInt()
            if (!groups.isEmpty) {
                start = groups.documents[groups.size()].toObject(Camp::class.java)!!.name.toInt() + 1


                val lastNumber = start + amount

                for (i in start..lastNumber) {

                    val group = Group(i.toString())
                    FirebaseUtils.addGroup(programID, group) { groupID ->

                        //finished adding group in database now start adding camps
                        startCampCreation(campNumber, groupID)
                    }
                }


            } else {
                for (i in 1..amount) {

                    val group = Group(i.toString())
                    FirebaseUtils.addGroup(programID, group) { groupID ->

                        //finished adding group in database now start adding camps
                        startCampCreation(campNumber, groupID)
                    }
                }
            }


        }

    }

    private fun create_Program_Group_Camp() {
        val programName = binding.groupEdtTxt.editableText.toString().trim()
        if (TextUtils.isEmpty(programName)) {
            Toasty.error(requireContext(), "Please enter Program Name ").show()
        } else {
            startProgramCreation(programName)
        }
    }

    private fun startProgramCreation(programName: String) {

        val groupNumber = binding.groupEdtTxt.editableText.toString().trim()
        val campNumber = binding.campEdtTxt.editableText.toString().trim()


        val program = Program(programName)
        FirebaseUtils.addProgram(program) { programID ->
            //finished adding group in database now start adding camps
            startGroupCreation(groupNumber, campNumber, programID)
        }


    }


    private fun create_Camp() {
        val campNumber =
                binding.campEdtTxt.editableText.toString().trim()

        if (TextUtils.isDigitsOnly(campNumber)) {
            Toasty.error(requireContext(), "Please enter Number of Camps To create").show()
        } else {

            //start camp creation
            val groupID = groupNames.documents.get(binding.groupSpinner.selectedItemPosition).id
            startCampCreation(campNumber, groupID)


        }

    }

    private fun startCampCreation(campNumber: String, groupID: String) {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id
        FirebaseUtils.getCampNames(programID, groupID) { camps ->
            campNames = camps
            var start: Int
            val amount = campNumber.toInt()

            if (!camps.isEmpty) {
                start = camps.documents[camps.size()].toObject(Camp::class.java)!!.name.toInt() + 1


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
}