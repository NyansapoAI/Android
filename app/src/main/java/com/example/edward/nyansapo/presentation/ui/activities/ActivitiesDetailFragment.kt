package com.example.edward.nyansapo.presentation.ui.activities

import android.os.Bundle


import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentIndividualActivitiesBinding

class ActivitiesDetailFragment: Fragment(R.layout.fragment_individual_activities) {


    private val TAG = "IndividualActivitiesFra"

    lateinit var binding: FragmentIndividualActivitiesBinding
    lateinit var activity: Activity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIndividualActivitiesBinding.bind(view)

        activity = requireArguments().getParcelable("activity")
        Log.d(TAG, "onViewCreated: activity:$activity")

        setDefaultValues()

    }

    private fun setDefaultValues() {
        binding.learningObjectivesTxtView.text = activity.learningObjectives
        binding.stepsTxtView.text = activity.steps
        binding.materialsTxtView.text = activity.materials
    }
}