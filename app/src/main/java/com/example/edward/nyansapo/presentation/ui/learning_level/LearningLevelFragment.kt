package com.example.edward.nyansapo.presentation.ui.learning_level

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.FragmentLearningLevelBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.example.edward.nyansapo.registerStudent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_learning_level.*

class LearningLevelFragment:Fragment(R.layout.fragment_learning_level) {


    private val TAG = "LearningLevelFragment"
    lateinit var adapter: LearningLevelAdapter


    lateinit var binding: FragmentLearningLevelBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLearningLevelBinding.bind(view)
        setUpToolbar()
        setUpTabLayout()




        checkIfTheDatabaseIsEmpty()
        initRecyclerViewAdapter()
        setSwipeListenerForItems()


    }

    private fun initRecyclerViewAdapter() {
        val sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(requireContext(), "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }


        val query: Query = FirebaseUtils.getCollectionStudentFromCamp_ReturnCollection(programId, groupId, campId)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java)
                .setLifecycleOwner(this).build()


        adapter = LearningLevelAdapter(this, firestoreRecyclerOptions, {
            onStudentClicked(it)
        })
        recyclerview.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerview.setAdapter(adapter)

    }

    private fun checkIfTheDatabaseIsEmpty() {
        val sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(requireContext(), "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        FirebaseUtils.getCollectionStudentFromCamp_ReturnSnapshot(programId, groupId, campId) {
            if (it.isEmpty) {
                MaterialAlertDialogBuilder(requireContext()).setBackground(requireActivity().getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_add_24).setTitle("Add").setMessage("Do You want To add Student ").setNegativeButton("no") { dialog, which ->
                }.setPositiveButton("yes") { dialog, which -> addstudent() }.show()

            }
        }
    }

    private fun onStudentClicked(it: DocumentSnapshot) {
        Log.d(TAG, "onStudentClicked: student Has been clicked")
    }


    private fun setSwipeListenerForItems() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter?.deleteFromDatabase(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerview)
    }

    private fun setUpTabLayout() {
        binding.tabs.addTab(binding.tabs.newTab().setText("Beginner"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Letter"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Word"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Paragraph"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Story"))


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

        val myIntent = Intent(requireContext(), registerStudent::class.java)
        startActivity(myIntent)
    }


}