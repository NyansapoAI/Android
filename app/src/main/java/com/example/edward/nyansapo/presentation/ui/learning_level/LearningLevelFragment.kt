package com.example.edward.nyansapo.presentation.ui.learning_level

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.dataBaseHandler
import com.example.edward.nyansapo.databinding.FragmentLearningLevelBinding
import com.example.edward.nyansapo.presentation.utils.TabEnum
import com.example.edward.nyansapo.registerStudent
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_learning_level.*

class LearningLevelFragment:Fragment(R.layout.fragment_learning_level) {

    companion object {
        private const val TAG = "LearningLevelFragment"
    }

    lateinit var binding: FragmentLearningLevelBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLearningLevelBinding.bind(view)
        setUpToolbar()
        setUpViewPager()
    }

    private fun setUpToolbar() {
        binding.toolbar.inflateMenu(R.menu.learning_level_menu)
        binding.toolbar.setTitle("Grouping")
        binding.toolbar.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.addStudentItem -> {
                    addstudent()
                }
            }








            true
        }
    }


    fun addstudent() {

        val databaseHander = dataBaseHandler(requireContext())
        val instructor = databaseHander.instructor
     //  val instructor_id = instructor.getCloud_id()

        var instructor_id = "123"


        val myIntent = Intent(requireContext(), registerStudent::class.java)
        myIntent.putExtra("instructor_id", instructor_id)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
    }

    fun setUpViewPager() {

        binding.viewpager!!.adapter = ViewPagerAdapter(requireActivity(), TabEnum.LEARNING_LEVELS) //Attach the adapter with our ViewPagerAdapter passing the host activity
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