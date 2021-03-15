package com.example.edward.nyansapo.presentation.ui.home

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentHomePageBinding
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class HomePageFragment : Fragment(R.layout.fragment_home_page) {

    var programCheck = 0
    var groupCheck = 0

    lateinit var programNames: QuerySnapshot
    lateinit var groupNames: QuerySnapshot
    lateinit var campNames: QuerySnapshot

    lateinit var binding: FragmentHomePageBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomePageBinding.bind(view)
        setOnClickListeners()
        setItemClickListener()
        fetchData()
    }

    private fun fetchData() {
        FirebaseUtils.getProgramNames { programs ->
            programNames = programs

            val spinnerValue = programs.map {
                it.toObject(Program::class.java).name
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
            binding.programNameSpinner.setAdapter(arrayAdapter)




        }
    }

    private fun setItemClickListener() {

        binding.programNameSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++programCheck > 1) {
                    startFetchingSpecificGroup()

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
        binding.groupSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++groupCheck > 1) {
                    startFetchingSpecificCamp(groupNames.documents[i])

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })


    }

    private fun startFetchingSpecificCamp(documentSnapshot: DocumentSnapshot?) {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id
        val groupID = groupNames.documents.get(binding.groupSpinner.selectedItemPosition).id
        FirebaseUtils.getCampNames(programID,groupID) { camps ->
            campNames = camps

            val spinnerValue = camps.map {
                it.toObject(Camp::class.java).name
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)
            binding.campSpinner.setAdapter(arrayAdapter)


        }
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

    private fun setOnClickListeners() {
        binding.createFob.setOnClickListener {
            //go to create new page
        }
    }

}