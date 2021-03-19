package com.example.edward.nyansapo.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.ActivityMain2Binding
import com.example.edward.nyansapo.presentation.ui.activities.ActivitiesFragment
import com.example.edward.nyansapo.presentation.ui.assessment.AssessmentFragment
import com.example.edward.nyansapo.presentation.ui.home.HomePageFragment
import com.example.edward.nyansapo.presentation.ui.learning_level.LearningLevelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity2 : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity2"
    }

    lateinit var binding: ActivityMain2Binding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //set default selected item
        binding.bottomNavigation.selectedItemId = R.id.action_home


    }

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.action_activities -> {
                Log.d(TAG, "activities clicked: ")

                supportFragmentManager.beginTransaction().replace(R.id.container, ActivitiesFragment()).commit()

            }
            R.id.action_grouping -> {
                Log.d(TAG, "grouping clicked: ")

                supportFragmentManager.beginTransaction().replace(R.id.container, LearningLevelFragment()).commit()

            }
            R.id.action_home -> {
                Log.d(TAG, "home clicked: ")
                supportFragmentManager.beginTransaction().replace(R.id.container, HomePageFragment()).commit()

            }
            R.id.action_assess -> {
                Log.d(TAG, "assessment clicked: ")
                supportFragmentManager.beginTransaction().replace(R.id.container, AssessmentFragment()).commit()
            }
            /*
            R.id.action_grouping ->{
                binding.viewpager.setCurrentItem(9)
                setTitle("Learning Level")
            }


            R.id.action_patterns ->{
                binding.viewpager.setCurrentItem(10)
                setTitle("Data Analytics")

            }*/
         }
        true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.learning_level_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

/*    fun setUpViewPager() {

        binding.viewpager!!.adapter = ViewPagerAdapter(this,TabScreensFragment()) //Attach the adapter with our ViewPagerAdapter passing the host activity
        TabLayoutMediator(tabs, binding.viewpager!!
        ) { tab, position ->
            if(position<6){
                tab.text = (binding.viewpager!!.adapter as ViewPagerAdapter?)!!.mFragmentNames[position] //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.

            }
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

    }*/

}