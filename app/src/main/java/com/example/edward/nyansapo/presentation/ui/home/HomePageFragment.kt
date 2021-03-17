package com.example.edward.nyansapo.presentation.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentHomePageBinding
import com.example.edward.nyansapo.presentation.ui.attendance.AttendanceFragment
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
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


        binding = FragmentHomePageBinding.bind(view)
        Log.d(TAG, "onViewCreated: ")
        initProgressBar()
        sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
        setOnClickListeners()
        setItemClickListener()

        Log.d(TAG, "onViewCreated: ${campSpinner.selectedItemPosition}")
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        fetchProgramNames()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    private fun fetchProgramNames() {
        listenerRegistrationProgram = FirebaseUtils.getProgramNamesContinuously() { programs ->
            programNames = programs

            if (programs.isEmpty) {
                showToast("Please First create A program")
                return@getProgramNamesContinuously
            } else {
                val spinnerValue = programs.map {
                    "Program: ${it.toObject(Program::class.java).number}"
                }
                // val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValue)

                val adapter = SpinnerAdapter(requireContext(), programNames, spinnerValue, {
                    deleteItem(it)

                }) { documentReference, documentSnapshot ->
                    val program = documentSnapshot.toObject(Program::class.java)
                    editItem(TYPE_PROGRAM, documentReference, program!!)
                }



                binding.programNameSpinner.setAdapter(adapter)
                //set default value of program
                setDefaultProgram()

                startFetchingSpecificGroup()
            }


        }
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

        Toasty.error(requireContext(), message, Toasty.LENGTH_LONG).show()

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


                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })


    }

    private fun updateProgramSharedPref() {
        Log.d(TAG, "updateProgramSharedPref: updating program shared pref")
        val programID = programNames.documents[binding.programNameSpinner.selectedItemPosition].id
        val programPos = binding.programNameSpinner.selectedItemPosition
        sharedPreferences.edit().putString(Constants.KEY_PROGRAM_ID, programID).putInt(Constants.PROGRAM_POS, programPos).apply()

    }

    private fun updateGroupSharedPref() {
        Log.d(TAG, "updateGroupSharedPref: updating group")
        val groupId = groupNames.documents[binding.groupSpinner.selectedItemPosition].id
        val groupPos = binding.groupSpinner.selectedItemPosition
        sharedPreferences.edit().putString(Constants.KEY_GROUP_ID, groupId).putInt(Constants.GROUP_POS, groupPos).apply()

    }

    private fun updateCampSharedPref() {
        Log.d(TAG, "updateCampSharedPref: updating camp")
        val campId = campNames.documents[binding.campSpinner.selectedItemPosition].id
        val campPos = binding.campSpinner.selectedItemPosition
        sharedPreferences.edit().putString(Constants.KEY_CAMP_ID, campId).putInt(Constants.CAMP_POS, campPos).apply()

    }

    private fun startFetchingSpecificCamp() {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id
        val groupID = groupNames.documents.get(binding.groupSpinner.selectedItemPosition).id
        listenerRegistrationCamp = FirebaseUtils.getCampNamesContinously(programID, groupID) { camps ->
            campNames = camps

            val spinnerValue = camps.map {
                "Camp: ${it.toObject(Camp::class.java).number}"

            }
            val adapter = SpinnerAdapter(requireContext(), campNames, spinnerValue, { deleteItem(it) }) { documentReference, documentSnapshot ->
                val camp = documentSnapshot.toObject(Camp::class.java)

                editItem(TYPE_CAMP, documentReference, camp!!)
            }
            binding.campSpinner.setAdapter(adapter)
            setDefaultCamp()


        }
    }

    private fun startFetchingSpecificGroup() {
        val programID = programNames.documents.get(binding.programNameSpinner.selectedItemPosition).id

        if (programNames.size()==1){
            Toasty.info(requireContext(),"You Only have one Program").show()

        }

        listenerRegistrationGroup = FirebaseUtils.getGroupNamesContinously(programID) { groups ->
            groupNames = groups


            val spinnerValue = groups.map {
                "Group: ${it.toObject(Group::class.java).number}"
            }
            val adapter = SpinnerAdapter(requireContext(), groupNames, spinnerValue, { deleteItem(it) }) { documentReference, documentSnapshot ->
                val group = documentSnapshot.toObject(Group::class.java)

                editItem(TYPE_GROUP, documentReference, group!!)
            }
            binding.groupSpinner.setAdapter(adapter)
            setDefaultGroup()

            if (!groups.isEmpty) {
                startFetchingSpecificCamp()
            }

        }

    }

    private fun setOnClickListeners() {


        binding.attendanceBtn.setOnClickListener {
            attendanceBtnClicked()
        }





        binding.createFob.setOnClickListener {
            //go to create new page
            Log.d(TAG, "setOnClickListeners: ")

            val intent = Intent(requireContext(), CreateNewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun attendanceBtnClicked() {
        val attendanceFragment = AttendanceFragment()

        val bundle = getInfoBundle()
        if (bundle == null) {
            return
        }

        attendanceFragment.arguments = bundle

        requireActivity()
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
        val campID = campNames.documents[binding.campSpinner.selectedItemPosition].id

        val bundle = bundleOf(Constants.KEY_PROGRAM_ID to programID, Constants.KEY_GROUP_ID to groupID, Constants.KEY_CAMP_ID to campID)
        return bundle
    }

    fun setDefaultProgram() {
        Log.d(TAG, "setDefaultProgram: ")
        val programPos = sharedPreferences.getInt(Constants.PROGRAM_POS, AdapterView.INVALID_POSITION)

        if (programPos == AdapterView.INVALID_POSITION || programPos >= programNames.size()) {
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

        if (campPos == AdapterView.INVALID_POSITION || campPos >= campNames.size()) {
            return
        }

        binding.campSpinner.setSelection(campPos)

    }


    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: called")
        if (this::listenerRegistrationProgram.isInitialized) {
            listenerRegistrationProgram.remove()

        }
        if (this::listenerRegistrationGroup.isInitialized) {
            listenerRegistrationGroup.remove()

        }
        if (this::listenerRegistrationCamp.isInitialized) {
            listenerRegistrationCamp.remove()

        }

    }

    fun createAlertDialog(title: String, message: String, documentReference: DocumentReference, organisation: Organisation) {

        val edittext = EditText(requireContext())
        edittext.setTextColor(Color.WHITE)
        edittext.setText(organisation.name)

        MaterialAlertDialogBuilder(requireContext())
                .setBackground(requireActivity().getDrawable(R.drawable.bg_dialog)).setIcon(R.drawable.ic_edit)
                .setTitle(title).setMessage(message).setView(edittext)
                .setNegativeButton("Cancel") { dialog, which -> //
                    // what to execute on cancel
                }.setPositiveButton("Save") { dialog, which ->
                    val string = edittext.text.toString()
                    val map = mapOf("name" to string)
                    updateOrganisation(map, documentReference)

                }.show()


        /*   val alert = AlertDialog.Builder(requireContext())

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

        dialog = setProgressDialog(requireContext(), "Loading..")
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