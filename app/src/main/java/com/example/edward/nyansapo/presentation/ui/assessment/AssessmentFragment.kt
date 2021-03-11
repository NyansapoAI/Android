package com.example.edward.nyansapo.presentation.ui.assessment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentActivitiesBinding
import com.example.edward.nyansapo.databinding.FragmentAssessmentBinding
import com.example.edward.nyansapo.databinding.FragmentBeginAssessmentBinding

class AssessmentFragment: Fragment(R.layout.fragment_assessment) {

    lateinit var binding:FragmentAssessmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentAssessmentBinding.bind(view)
    }
}