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
       // (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
      //  setHasOptionsMenu(true)
        setUpToolbar()
        setUpViewPager()
    }

    private fun setUpToolbar() {

        binding.toolbar.inflateMenu(R.menu.overflow_menu)
        binding.toolbar.setTitle("Activities")
        toolbar.setOnMenuItemClickListener { item->


            true

        }
    }


    fun setUpViewPager() {

        binding.viewpager!!.adapter = ViewPagerAdapter(requireActivity(), TabEnum.ACTIVITES) //Attach the adapter with our ViewPagerAdapter passing the host activity
        TabLayoutMediator(tabs, binding.viewpager!!
        ) { tab, position ->

            tab.text = (binding.viewpager!!.adapter as ViewPagerAdapter?)!!.mFragmentNames[position] //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.


        }.attach()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position
                Log.d(TAG, "onTabSelected: $position")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }


}