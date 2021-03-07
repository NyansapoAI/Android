package com.example.edward.nyansapo.presentation.ui.individual_activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentActivitiesBinding
import com.example.edward.nyansapo.databinding.FragmentIndividualActivitiesBinding

class IndividualActivitiesFragment: Fragment(R.layout.fragment_individual_activities) {

    lateinit var binding:FragmentIndividualActivitiesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentIndividualActivitiesBinding.bind(view)
    }
}