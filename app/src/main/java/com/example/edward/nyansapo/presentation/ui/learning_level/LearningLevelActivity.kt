package com.example.edward.nyansapo.presentation.ui.learning_level


import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.ActivityLearningLevelBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_learning_level.*


class LearningLevelActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LearningLevelActivity"
    }

    lateinit var binding: ActivityLearningLevelBinding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setUpViewPager()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.learning_level_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun setUpViewPager() {

        binding.viewpager!!.adapter = ViewPagerAdapter(this) //Attach the adapter with our ViewPagerAdapter passing the host activity
        TabLayoutMediator(tabs, binding.viewpager!!
        ) { tab, position ->
            tab.text = (binding.viewpager!!.adapter as ViewPagerAdapter?)!!.mFragmentNames[position] //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.
        }.attach()

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE)
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