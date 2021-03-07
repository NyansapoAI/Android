package com.example.edward.nyansapo.presentation.ui.data_analytics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentActivitiesBinding
import com.example.edward.nyansapo.databinding.FragmentDataAnalyticsBinding

class DataAnalyticsFragment: Fragment(R.layout.fragment_data_analytics) {

    lateinit var binding:FragmentDataAnalyticsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentDataAnalyticsBinding.bind(view)
    }
}