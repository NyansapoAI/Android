package com.example.edward.nyansapo.presentation.ui.learning_level

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fa: FragmentActivity?) : FragmentStateAdapter(fa!!) {
    val mFragments: Array<Fragment> = arrayOf<Fragment>(

           LearningLevelFragment(),
            LearningLevelFragment(),
            LearningLevelFragment(),
            LearningLevelFragment(),
            LearningLevelFragment(),
            LearningLevelFragment()
    )
    val mFragmentNames = arrayOf( //Tabs names array
            "Letters",
            "Word",
            "Paragraph",
            "Story",
            "Cohort",
            "Unknown",

            )

    override fun getItemCount(): Int {
        return mFragments.size //Number of fragments displayed
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }




    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }
}