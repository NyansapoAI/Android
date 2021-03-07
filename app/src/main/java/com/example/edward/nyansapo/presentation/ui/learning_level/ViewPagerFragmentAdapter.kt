package com.example.edward.nyansapo.presentation.ui.learning_level

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.edward.nyansapo.presentation.ui.activities.TabScreensFragment
import com.example.edward.nyansapo.presentation.utils.TabEnum


class ViewPagerAdapter(fa: FragmentActivity?, val tabEnum: TabEnum) : FragmentStateAdapter(fa!!) {

    val mFragments: MutableList<Fragment> = mutableListOf<Fragment>()


    init {
         when(tabEnum){
            TabEnum.ACTIVITES-> {
                repeat(6) {
                    mFragments.add(TabScreensFragment())
                }
            }
            TabEnum.LEARNING_LEVELS-> {
                repeat(6) {
                    mFragments.add(TabScreenLearningFragment())
                }
            }
        }

    }

    val mFragmentNames = arrayOf(
            //Tabs names array
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