package com.nyansapo.presentation.ui.grouping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nyansapo.Learning_Level
import com.nyansapo.R
import com.nyansapo.Student
import com.nyansapo.databinding.FragmentLearningLevelBinding
import com.nyansapo.presentation.ui.main.MainActivity2
import com.nyansapo.presentation.ui.student.StudentAssessmentListFragment
import com.nyansapo.presentation.utils.Constants
import com.nyansapo.presentation.utils.FirebaseUtils
import com.nyansapo.AddStudentFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_learning_level.*

class GroupingFragment : Fragment(R.layout.fragment_learning_level), SwipeListener {


    private val TAG = "LearningLevelFragment"
    lateinit var adapter: GroupingAdapter


    lateinit var binding: FragmentLearningLevelBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLearningLevelBinding.bind(view)
        setUpToolbar()
        setUpTabLayout()
        setOnClickListeners()
        setGestureListener()




        checkIfTheDatabaseIsEmpty()
        initRecyclerViewAdapter()
        //  setSwipeListenerForItems()


    }

    private fun setGestureListener() {
        binding.recyclerview.setOnTouchListener(SwipeGestureListener(this))
    }

    private fun setOnClickListeners() {
        binding.fob.setOnClickListener {
            addstudent()
        }
    }


    private fun initRecyclerViewAdapter(learningLevel: String = Learning_Level.UNKNOWN.name) {
        Log.d(TAG, "initRecyclerViewAdapter: ")
        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        Log.d(TAG, "initRecyclerViewAdapter: programid $programId  groupid $groupId campid $campId}")

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }


        val query: Query = FirebaseUtils.getCollectionStudentFromCamp_ReturnCollection(programId, groupId, campId).whereEqualTo("learningLevel", learningLevel)


        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java)
                .setLifecycleOwner(viewLifecycleOwner).build()


        adapter = GroupingAdapter(this, firestoreRecyclerOptions, {
            onStudentClicked(it)
        }) {
            onStudentLongClicked(it)
        }


        binding.recyclerview.setLayoutManager(LinearLayoutManager(MainActivity2.activityContext!!))
        binding.recyclerview.setAdapter(adapter)
        Log.d(TAG, "initRecyclerViewAdapter: adapter set up")


    }

    private fun onStudentLongClicked(it: DocumentSnapshot) {
        MaterialAlertDialogBuilder(MainActivity2.activityContext!!).setBackground(MainActivity2.activityContext!!.getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_delete).setTitle("delete").setMessage("Are you sure you want to delete ").setNegativeButton("no") { dialog, which -> }.setPositiveButton("yes") { dialog, which -> deleteData(it) }.show()

    }

    private fun deleteData(it: DocumentSnapshot) {
        it.reference.delete().addOnSuccessListener {
            Log.d(TAG, "deleteData: deletion success")
        }

    }

    private fun checkIfTheDatabaseIsEmpty() {
        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }

        FirebaseUtils.getCollectionStudentFromCamp_ReturnSnapshot(programId, groupId, campId) {

            if (it.isEmpty) {
                Log.d(TAG, "checkIfTheDatabaseIsEmpty: database is empty")

                if (context != null) {
                    Log.d(TAG, "checkIfTheDatabaseIsEmpty: context is null")
                    MaterialAlertDialogBuilder(MainActivity2.activityContext!!).setBackground(MainActivity2.activityContext?.getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_add_24).setTitle("Add").setMessage("Do You want To add Student ").setNegativeButton("no") { dialog, which ->
                    }.setPositiveButton("yes") { dialog, which -> addstudent() }.show()

                }

            } else {
                Log.d(TAG, "checkIfTheDatabaseIsEmpty: database has ${it.size()} students")
            }
        }
    }

    private fun onStudentClicked(it: DocumentSnapshot) {
        Log.d(TAG, "onStudentClicked: student Has been clicked")
        MainActivity2.activityContext!!.supportFragmentManager.beginTransaction().replace(R.id.container, StudentAssessmentListFragment()).addToBackStack(null).commit()
    }


    private fun setSwipeListenerForItems() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter?.deleteFromDatabase(viewHolder.bindingAdapterPosition)
            }
        }).attachToRecyclerView(recyclerview)
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
                thisTabPositionHasBeenSelected(position!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


    }

    private fun thisTabPositionHasBeenSelected(position: Int) {
        when (position) {
            0 -> {
                initRecyclerViewAdapter(Learning_Level.UNKNOWN.name)
            }
            1 -> {
                initRecyclerViewAdapter(Learning_Level.BEGINNER.name)
            }
            2 -> {
                initRecyclerViewAdapter(Learning_Level.LETTER.name)
            }
            3 -> {
                initRecyclerViewAdapter(Learning_Level.WORD.name)
            }
            4 -> {
                initRecyclerViewAdapter(Learning_Level.PARAGRAPH.name)
            }
            5 -> {
                initRecyclerViewAdapter(Learning_Level.STORY.name)
            }
            6 -> {
                initRecyclerViewAdapter(Learning_Level.ABOVE.name)
            }
        }


    }

    private fun setUpToolbar() {
        binding.toolbar.root.inflateMenu(R.menu.learning_level_menu)
        binding.toolbar.root.setTitle("Grouping")
        binding.toolbar.root.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.addStudentItem -> {
                    addstudent()
                }
            }


            true
        }
    }


    fun addstudent() {

        val myIntent = Intent(MainActivity2.activityContext!!, AddStudentFragment::class.java)
        startActivity(myIntent)
    }

    override fun onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft: ")
        Log.d(TAG, "onSwipeLeft: selectedTabPosition:${tabs.selectedTabPosition} : :tab size:${tabs.tabCount}")

        val position = (tabs.selectedTabPosition + 1) % tabs.tabCount
        tabs.getTabAt(position)!!.select()
        Log.d(TAG, "onSwipeLeft: position:$position")
    }

    override fun onSwipeRight() {
        Log.d(TAG, "onSwipeRight: ")
        Log.d(TAG, "onSwipeRight: selectedTabPosition:${tabs.selectedTabPosition} : :tab size:${tabs.tabCount}")

        Log.d(TAG, "onSwipeRight: ")
        var position = (tabs.selectedTabPosition - 1) % tabs.tabCount
        if (position < 0) {
            position = tabs.tabCount - 1
        }
        tabs.getTabAt(position)!!.select()
        Log.d(TAG, "onSwipeRight: position:$position")


    }
}