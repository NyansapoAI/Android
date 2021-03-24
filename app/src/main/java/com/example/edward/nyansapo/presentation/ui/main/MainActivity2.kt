package com.example.edward.nyansapo.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.ActivityMain2Binding
import com.example.edward.nyansapo.presentation.ui.activities.ActivitiesFragment
import com.example.edward.nyansapo.presentation.ui.assessment.AssessmentFragment
import com.example.edward.nyansapo.presentation.ui.data_analytics.DataAnalyticsFragment
import com.example.edward.nyansapo.presentation.ui.home.HomePageFragment
import com.example.edward.nyansapo.presentation.ui.learning_level.LearningLevelFragment
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import es.dmoral.toasty.Toasty
import java.io.File


class MainActivity2 : AppCompatActivity() {



    companion object {
        private const val TAG = "MainActivity2"
        @JvmField
        var activityContext: MainActivity2?=null

    }

    lateinit var binding: ActivityMain2Binding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        activityContext = this
        setUpNavigationDrawer()

        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //set default selected item
        binding.bottomNavigation.selectedItemId = R.id.action_home


    }

    private fun setUpNavigationDrawer() {
        val toolbar = binding.root.findViewById<Toolbar>(R.id.toolbar)
        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        binding.drawerLayout.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(drawerListener)
    }

    val drawerListener = object : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {

            when (item.itemId) {
                R.id.exportDataItem -> {
                    exportData()
                }
                R.id.settingsItem -> {

                }
                R.id.tutorialItem -> {

                }
                R.id.logoutItem -> {
                    logoutClicked()
                }
            }

            val drawer = binding.drawerLayout
            drawer.closeDrawer(GravityCompat.START)
            return true
        }
    }

    private fun logoutClicked() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            Log.d(TAG, "logoutClicked: sucess")
            finish()
        }
    }

    val onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {

            val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

            val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
            val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
            val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
            val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

            Log.d(TAG, "onNavigationItemSelected: programId:$programId: :groupId:$groupId: :campId:$campId")

            if (campId == null) {

                if (item.itemId == R.id.action_home) {

                    supportFragmentManager.beginTransaction().replace(R.id.container, HomePageFragment()).commit()
                    return true
                }

                Toasty.error(this@MainActivity2, "Please First create A camp", Toasty.LENGTH_LONG).show()
                return false
            } else {


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


                    R.id.action_patterns -> {
                        Log.d(TAG, "patterns clicked: ")
                        supportFragmentManager.beginTransaction().replace(R.id.container, DataAnalyticsFragment()).commit()

                    }
                }
            }
            return true
        }
    }

    /*  val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

          val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

          val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
          val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
          val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
          val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

          if (campPos == -1) {
              Toasty.error(this, "Please First create A camp", Toasty.LENGTH_LONG).show()
              false
          } else {


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


                  R.id.action_patterns -> {
                      Log.d(TAG, "patterns clicked: ")
                      supportFragmentManager.beginTransaction().replace(R.id.container, DataAnalyticsFragment()).commit()

                  }
              }
          }








          true
      }*/

    fun exportData() {
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(this, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            return
        }

        FirebaseUtils.getCollectionStudentFromCamp_ReturnSnapshot(programId, groupId, campId) {

            val students = it.toObjects(Student::class.java)
            val data = StringBuilder()
            data.append("Firstname,Lastname,Age,Gender,Class,Learning_Level") // generate headers
            for (student in students!!) { // generate csv data
                data.append("""
    
    ${student.firstname},${student.lastname},${student.age},${student.gender},${student.std_class},${student.learningLevel}
    """.trimIndent())
            }
            try {
                // save file before sending
                val out = openFileOutput("NyansapoData.csv", MODE_PRIVATE)
                out.write(data.toString().toByteArray())
                out.close()

                // export file
                val context = applicationContext
                val filelocation = File(filesDir, "NyansapoData.csv")
                val path = FileProvider.getUriForFile(context, "com.example.edward.nyansapo.fileprovider", filelocation)
                val fileIntent = Intent(Intent.ACTION_SEND)
                fileIntent.type = "text/csv"
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Nyansapo Data")
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                fileIntent.putExtra(Intent.EXTRA_STREAM, path)
                startActivity(Intent.createChooser(fileIntent, "Export Data"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.learning_level_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onBackPressed() {

        val fragmentCount = supportFragmentManager.backStackEntryCount
        Log.d(TAG, "onBackPressed: fragmentCount:$fragmentCount")

        val drawer = binding.drawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}