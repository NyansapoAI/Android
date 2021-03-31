package com.nyansapo.presentation.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.nyansapo.Assessment_Content
import com.nyansapo.R
import com.nyansapo.databinding.FragmentHomePageBinding
import com.nyansapo.presentation.ui.attendance.AttendanceFragment
import com.nyansapo.presentation.ui.main.MainActivity2
import com.nyansapo.presentation.utils.Constants
import com.nyansapo.presentation.utils.FirebaseUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_home_page.*


class HomePageFragment : Fragment(R.layout.fragment_home_page) {
    lateinit var sharedPreferences: SharedPreferences

    val TYPE_PROGRAM = 0
    val TYPE_GROUP = 1
    val TYPE_CAMP = 2

    private val TAG = "HomePageFragment"
    var programCheck = 0
    var groupCheck = 0

    lateinit var programNames: QuerySnapshot
    lateinit var groupNames: QuerySnapshot
    lateinit var campNames: QuerySnapshot
    lateinit var listenerRegistrationProgram: ListenerRegistration
    lateinit var listenerRegistrationGroup: ListenerRegistration
    lateinit var listenerRegistrationCamp: ListenerRegistration
    lateinit var binding: FragmentHomePageBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ////////////////////test
        doTest()


        ///////////////test


        binding = FragmentHomePageBinding.bind(view)
        Log.d(TAG, "onViewCreated: ")
        initProgressBar()
        sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
        setOnClickListeners()
        setItemClickListener()

        Log.d(TAG, "onViewCreated: ${campSpinner.selectedItemPosition}")
    }

    private fun doTest() {
        var sentenceList = Assessment_Content.s3!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")

        sentenceList = Assessment_Content.s4!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")
        sentenceList = Assessment_Content.s5!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")
        sentenceList = Assessment_Content.s6!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")
        sentenceList = Assessment_Content.s7!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")
        sentenceList = Assessment_Content.s8!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")
        sentenceList = Assessment_Content.s9!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")
        sentenceList = Assessment_Content.s10!!.split(".")
                .filter { line ->
                    line.isNotBlank()
                }.map {
                    it.trim()
                }.toTypedArray()
        Log.d(TAG, "doTest: size:${sentenceList.size}")


    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        fetchProgramNames()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //setting main activity toolbar to visible
        (MainActivity2.activityContext!! as MainActivity2).binding.root.findViewById<Toolbar>(R.id.toolbar).isVisible = true
        (MainActivity2.activityContext!! as MainActivity2).binding.root.findViewById<Toolbar>(R.id.toolbar).title = "Home"
    }

    private fun fetchProgramNames() {

        listenerRegistrationProgram = FirebaseUtils.getProgramNamesContinuously() { programs ->
            programNames = programs


            val spinnerValue = programs.map {
                "Program: ${it.toObject(Program::class.java).number}"
            }

            val adapter = SpinnerAdapter(MainActivity2.activityContext!!, programNames, spinnerValue, {
                deleteItem(it)

            }) { documentReference, documentSnapshot ->
                val program = documentSnapshot.toObject(Program::class.java)
                editItem(TYPE_PROGRAM, documentReference, program!!)
            }


            binding.programNameSpinner.setAdapter(adapter)


            //set default value of program
            setDefaultProgram()
            setDataForDrawer()

            startFetchingSpecificGroup()

            if (programs.isEmpty) {
                adapter.notifyDataSetChanged()

                Log.d(TAG, "fetchProgramNames: programs is empty")
                showToast("Please First create A program")
                programsIsEmpty()

                setDataForDrawer()
            }


        }
    }

    private fun programsIsEmpty() {
        Log.d(TAG, "programsIsEmpty: ")

        binding.programNameSpinner.adapter = null
        groupsIsEmpty()
    }

    private fun editItem(type: Int, documentReference: DocumentReference, organisation: Organisation) {
        when (type) {
            TYPE_PROGRAM -> {
                createAlertDialog("Program", "Edit program: ${organisation.number} name", documentReference, organisation)
            }
            TYPE_GROUP -> {
                createAlertDialog("Group", "Edit group: ${organisation.number} name", documentReference, organisation)
            }
            TYPE_CAMP -> {
                createAlertDialog("Camp", "Edit camp: ${organisation.number} name", documentReference, organisation)
            }
        }

    }

    private fun showToast(message: String) {

        Toasty.info(MainActivity2.activityContext!!, message, Toasty.LENGTH_LONG).show()

    }

    private fun deleteItem(reference: DocumentReference) {
        Log.d(TAG, "deleteItem: started deleteing item")
        showProgress(true)
        reference.delete().addOnSuccessListener {
            Log.d(TAG, "deleteItem: deletion success")

            showProgress(false)
        }

    }

    private fun setItemClickListener() {

        binding.programNameSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                if (++programCheck > 1) {
                    Log.d(TAG, "onItemSelected: program spinner")
                    startFetchingSpecificGroup()

                    //saving programId to be accessed in other screens
                    updateProgramSharedPref()

                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
        binding.groupSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                Log.d(TAG, "onItemSelected:  group spinner")
                if (++groupCheck > 1) {
                    startFetchingSpecificCamp()

                    //saving groupId to be accessed in other screens
                    updateGroupSharedPref()


                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
        binding.campSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {

                Log.d(TAG, "onItemSelected: camp spinner current pos: ${campSpinner.selectedItemPosition}")
                if (++groupCheck > 1) {
                    //saving campId to be accessed in other screens
                    updateCampSharedPref()

                    setDataForDrawer()


                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })


    }

    private fun updateProgramSharedPref() {
        Log.d(TAG, "updateProgramSharedPref: updating program shared pref")
        try {
            val program = programNames.documents[binding.programNameSpinner.selectedItemPosition].toObject(Program::class.java)
            val programID = programNames.documents[binding.programNameSpinner.selectedItemPosition].id
            val programPos = binding.programNameSpinner.selectedItemPosition
            sharedPreferences.edit().putString(Constants.KEY_PROGRAM_ID, programID).putInt(Constants.PROGRAM_POS, programPos).apply()

            Log.d(TAG, "updateProgramSharedPref: :program:$program")

        } catch (e: Exception) {
            e.printStackTrace()
            sharedPreferences.edit().putString(Constants.KEY_PROGRAM_ID, null).putInt(Constants.PROGRAM_POS, -1).apply()

        }


    }

    private fun updateGroupSharedPref() {
        Log.d(TAG, "updateGroupSharedPref: updating group")

        try {
            val group = groupNames.documents[binding.groupSpinner.selectedItemPosition].toObject(Group::class.java)
            val groupId = groupNames.documents[binding.groupSpinner.selectedItemPosition].id
            val groupPos = binding.groupSpinner.selectedItemPosition
            sharedPreferences.edit().putString(Constants.KEY_GROUP_ID, groupId).putInt(Constants.GROUP_POS, groupPos).apply()
            Log.d(TAG, "updateGroupSharedPref: group:$group")

        } catch (e: Exception) {
            e.printStackTrace()
            sharedPreferences.edit().putString(Constants.KEY_GROUP_ID, null).putInt(Constants.GROUP_POS, -1).apply()


        }
    }

    private fun updateCampSharedPref() {
        Log.d(TAG, "updateCampSharedPref: updating camp")


        try {
            val camp = campNames!!.documents[binding.campSpinner.selectedItemPosition].toObject(Camp::class.java)
            val campId = campNames!!.documents[binding.campSpinner.selectedItemPosition].id
            val campPos = binding.campSpinner.selectedItemPosition
            sharedPreferences.edit().putString(Constants.KEY_CAMP_ID, campId).putInt(Constants.CAMP_POS, campPos).apply()

            Log.d(TAG, "updateCampSharedPref: camp:$camp")

        } catch (e: Exception) {
            e.printStackTrace()
            sharedPreferences.edit().putString(Constants.KEY_CAMP_ID, null).putInt(Constants.CAMP_POS, -1).apply()

        }

    }

    private fun updateSharedPreference() {
        updateProgramSharedPref()
        updateGroupSharedPref()
        updateCampSharedPref()
    }

    private fun startFetchingSpecificCamp() {


        if (!programNames.isEmpty && !groupNames.isEmpty) {
            val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id
            val groupID = groupNames.documents.get(binding.groupSpinner.selectedItemPosition).id
            listenerRegistrationCamp = FirebaseUtils.getCampNamesContinously(programID, groupID) { camps ->
                campNames = camps

                val spinnerValue = camps.map {
                    "Camp: ${it.toObject(Camp::class.java).number}"

                }
                val adapter = SpinnerAdapter(MainActivity2.activityContext!!, campNames!!, spinnerValue, { deleteItem(it) }) { documentReference, documentSnapshot ->
                    val camp = documentSnapshot.toObject(Camp::class.java)

                    editItem(TYPE_CAMP, documentReference, camp!!)
                }
                binding.campSpinner.setAdapter(adapter)
                setDefaultCamp()

                //set Data For Drawer
                setDataForDrawer()
            }

        } else {
            setDataForDrawer()
            campIsEmpty()
        }


    }

    private fun campIsEmpty() {
        Log.d(TAG, "campIsEmpty: ")
        campSpinner.adapter = null

    }

    private fun setDataForDrawer() {
        val menu = (MainActivity2.activityContext!! as MainActivity2).binding.navView.menu
        FirebaseUtils.firebaseAuth.currentUser.apply {
            menu.findItem(R.id.instructorNameItem).title = "${this?.displayName} "
        }


        try {
            val programName = programNames.toObjects(Program::class.java)[binding.programNameSpinner.selectedItemPosition]
            menu.findItem(R.id.programNameItem).title = "Program ${programName.number}"
        } catch (e: Exception) {
            menu.findItem(R.id.programNameItem).title = "No Program Selected"
            e.printStackTrace()
        }

        try {
            val groupName = groupNames.toObjects(Group::class.java)[binding.groupSpinner.selectedItemPosition]
            menu.findItem(R.id.groupItem).title = "Group ${groupName.number}"

        } catch (e: Exception) {
            menu.findItem(R.id.groupItem).title = "No Group Selected"
            e.printStackTrace()
        }
        try {
            val campName = campNames!!.toObjects(Camp::class.java)[binding.campSpinner.selectedItemPosition]
            Log.d(TAG, "setDataForDrawer:campName:${campName.number} : :campSize:${campNames!!.size()} ::selected position:${binding.campSpinner.selectedItemPosition} ")

            menu.findItem(R.id.campNumberItem).title = "Camp ${campName.number}"

        } catch (e: Exception) {
            menu.findItem(R.id.campNumberItem).title = "No camp selected"
            e.printStackTrace()
        }

        updateSharedPreference()
    }

    private fun startFetchingSpecificGroup() {

        if (!programNames.isEmpty) {

            val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

            if (programNames.size() == 1) {
                //  Toasty.info(MainActivity2.activityContext!!, "You Only have one Program").show()
                Log.d(TAG, "startFetchingSpecificGroup: you only have one program")
            }

            listenerRegistrationGroup = FirebaseUtils.getGroupNamesContinously(programID) { groups ->
                groupNames = groups

                if (groupNames.isEmpty) {
                    campIsEmpty()
                }

                val spinnerValue = groups.map {
                    "Group: ${it.toObject(Group::class.java).number}"
                }
                val adapter = SpinnerAdapter(MainActivity2.activityContext!!, groupNames, spinnerValue, { deleteItem(it) }) { documentReference, documentSnapshot ->
                    val group = documentSnapshot.toObject(Group::class.java)

                    editItem(TYPE_GROUP, documentReference, group!!)
                }
                binding.groupSpinner.setAdapter(adapter)
                setDefaultGroup()
                setDataForDrawer()

                startFetchingSpecificCamp()
            }
        } else {
            Log.d(TAG, "startFetchingSpecificGroup: programNames is empty")
            setDataForDrawer()
            groupsIsEmpty()
        }
    }

    private fun groupsIsEmpty() {
        groupSpinner.adapter = null
        campIsEmpty()
    }

    private fun setOnClickListeners() {


        binding.attendanceBtn.setOnClickListener {
            attendanceBtnClicked()
        }





        binding.createFob.setOnClickListener {
            //go to create new page
            Log.d(TAG, "setOnClickListeners: ")


            MainActivity2.activityContext!!.supportFragmentManager.beginTransaction().replace(R.id.container, CreateFragment()).addToBackStack(null).commit()


        }
    }

    private fun attendanceBtnClicked() {
        val attendanceFragment = AttendanceFragment()

        val bundle = getInfoBundle()
        if (bundle == null) {
            return
        }

        attendanceFragment.arguments = bundle

        MainActivity2.activityContext!!
                .supportFragmentManager
                .beginTransaction().replace(R.id.container, attendanceFragment)
                .addToBackStack(null).commit()
    }

    private fun getInfoBundle(): Bundle? {
        binding.apply {
            if (binding.programNameSpinner.selectedItemPosition == AdapterView.INVALID_POSITION) {
                showToast("Please First Create A Program")
                return null
            }
            if (binding.groupSpinner.selectedItemPosition == AdapterView.INVALID_POSITION) {
                showToast("Please First Create A Program")
                return null
            }
            if (binding.campSpinner.selectedItemPosition == AdapterView.INVALID_POSITION) {
                showToast("Please First Create A Program")
                return null
            }

        }


        val programID = programNames.documents[binding.programNameSpinner.selectedItemPosition].id
        val groupID = groupNames.documents[binding.groupSpinner.selectedItemPosition].id
        val campID = campNames!!.documents[binding.campSpinner.selectedItemPosition].id

        val bundle = bundleOf(Constants.KEY_PROGRAM_ID to programID, Constants.KEY_GROUP_ID to groupID, Constants.KEY_CAMP_ID to campID)
        return bundle
    }

    fun setDefaultProgram() {
        Log.d(TAG, "setDefaultProgram: ")
        val programPos = sharedPreferences.getInt(Constants.PROGRAM_POS, AdapterView.INVALID_POSITION)

        if (programPos == AdapterView.INVALID_POSITION || programPos >= programNames.size()) {

            ///
            updateProgramSharedPref()

            return
        }

        binding.programNameSpinner.setSelection(programPos)

    }

    fun setDefaultGroup() {
        Log.d(TAG, "setDefaultGroup: ")
        val groupPos = sharedPreferences.getInt(Constants.GROUP_POS, AdapterView.INVALID_POSITION)

        if (groupPos == AdapterView.INVALID_POSITION || groupPos >= groupNames.size()) {
            return
        }

        binding.groupSpinner.setSelection(groupPos)

    }

    fun setDefaultCamp() {
        Log.d(TAG, "setDefaultCamp: ")
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, AdapterView.INVALID_POSITION)

        if (campPos == AdapterView.INVALID_POSITION || campPos >= campNames!!.size()) {
            return
        }

        binding.campSpinner.setSelection(campPos)

    }


    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: called")


        //setting main activity toolbar to invisible
        (MainActivity2.activityContext!! as MainActivity2).binding.root.findViewById<Toolbar>(R.id.toolbar).isVisible = false




        if (this::listenerRegistrationProgram.isInitialized) {
            listenerRegistrationProgram.remove()

        }
        if (this::listenerRegistrationGroup.isInitialized) {
            listenerRegistrationGroup.remove()

        }
        if (this::listenerRegistrationCamp.isInitialized) {
            listenerRegistrationCamp.remove()

        }

        if (!this::programNames.isInitialized || !this::groupNames.isInitialized || !this::campNames.isInitialized) {
            Log.d(TAG, "onStop: something is not initialized")
            return

        }
        if (programNames.isEmpty) {
            showToast("Please First create a program")
        }
        if (groupNames.isEmpty) {
            showToast("Please First create a group")
        }
        if (campNames!!.isEmpty) {
            showToast("Please First create a camp")
        }

        updateProgramSharedPref()
        updateGroupSharedPref()
        updateCampSharedPref()

    }

    fun createAlertDialog(title: String, message: String, documentReference: DocumentReference, organisation: Organisation) {

        val edittext = EditText(MainActivity2.activityContext!!)
        edittext.setTextColor(Color.WHITE)
        edittext.setText(organisation.name)

        MaterialAlertDialogBuilder(MainActivity2.activityContext!!)
                .setBackground(MainActivity2.activityContext!!.getDrawable(R.drawable.bg_dialog)).setIcon(R.drawable.ic_edit)
                .setTitle(title).setMessage(message).setView(edittext)
                .setNegativeButton("Cancel") { dialog, which -> //
                    // what to execute on cancel
                }.setPositiveButton("Save") { dialog, which ->
                    val string = edittext.text.toString()
                    val map = mapOf("name" to string)
                    updateOrganisation(map, documentReference)

                }.show()


        /*   val alert = AlertDialog.Builder(MainActivity2.activityContext!!)

           edittext.setText(organisation.name)

           alert.setTitle(title)
           alert.setMessage(message)
           alert.setIcon(R.drawable.ic_edit)

           alert.setView(edittext)

           alert.setPositiveButton("Save") { dialog, whichButton -> //What ever you want to do with the value
               val string = edittext.text.toString()
               val map = mapOf("name" to string)
               updateOrganisation(map, documentReference)
           }

           alert.setNegativeButton("Cancel") { dialog, whichButton ->
               // what ever you want to do with No option.
           }

           alert.show()*/
    }

    private fun updateOrganisation(map: Map<String, String>, documentReference: DocumentReference) {
        showProgress(true)
        Log.d(TAG, "updateOrganisation: started update")
        documentReference.set(map, SetOptions.merge()).addOnSuccessListener {
            Log.d(TAG, "updateOrganisation: update successfull")
            showProgress(false)
        }
    }

    /////////////////////PROGRESS_BAR////////////////////////////
    lateinit var dialog: AlertDialog

    private fun showProgress(show: Boolean) {

        if (show) {
            dialog.show()

        } else {
            dialog.dismiss()

        }

    }

    private fun initProgressBar() {

        dialog = setProgressDialog(MainActivity2.activityContext!!, "Loading..")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

    //end progressbar

}