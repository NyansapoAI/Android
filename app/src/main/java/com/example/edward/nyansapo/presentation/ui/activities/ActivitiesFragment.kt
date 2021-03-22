package com.example.edward.nyansapo.presentation.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentActivitiesBinding
import com.example.edward.nyansapo.presentation.ui.learning_level.ViewPagerAdapter
import com.example.edward.nyansapo.presentation.utils.TabEnum
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_learning_level.*


class ActivitiesFragment : Fragment(R.layout.fragment_activities) {
    companion object {
        private  const val TAG="ActivitiesFragment"
    }
    lateinit var binding: FragmentActivitiesBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        binding = FragmentActivitiesBinding.bind(view)


        setUpToolbar()
        setUpTabLayout()
        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        binding.fob.setOnClickListener {

        }
    }

    private fun setUpToolbar() {
        binding.toolbar.root.inflateMenu(R.menu.search_menu)
        binding.toolbar.root.setTitle("Activities")
        binding.toolbar.root.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.addStudentItem -> {
                   //
                }
            }


            true
        }
    }
    private fun setUpTabLayout() {
        binding.tabs.addTab(binding.tabs.newTab().setText("UNKNOWN"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Beginner"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Letter"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Word"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Paragraph"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Story"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Above"))


        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position
                Log.d(TAG, "onTabSelected: $position")
            //    thisTabPositionHasBeenSelected(position!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


    }

}