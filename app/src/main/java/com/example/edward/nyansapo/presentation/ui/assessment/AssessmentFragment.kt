package com.example.edward.nyansapo.presentation.ui.assessment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Filter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView.*
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.FragmentAssessmentBinding
import com.example.edward.nyansapo.presentation.ui.main.MainActivity2
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
import com.example.edward.nyansapo.AddStudentFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import es.dmoral.toasty.Toasty


class AssessmentFragment : Fragment(R.layout.fragment_assessment) {

    private val TAG = "AssessmentFragment"


    lateinit var binding: FragmentAssessmentBinding

    val FIND_SUGGESTION_SIMULATED_DELAY: Long = 250
    lateinit var mSearchView: FloatingSearchView
    lateinit var mainQuerySnapshot: QuerySnapshot
    val filteredQuerySnapshot: ArrayList<DocumentSnapshot> = ArrayList()

    lateinit var listenerRegistration: ListenerRegistration


    private var mIsDarkSearchTheme = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAssessmentBinding.bind(view)

        mSearchView = view.findViewById(R.id.mSearchView) as FloatingSearchView

        setupDrawer()
        setOnClickListeners()

        fetchAllStudentFirst {

            mainQuerySnapshot = it
            setupSearchBar()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAllStudentFirst {
            mainQuerySnapshot = it

        }
    }

    private fun setOnClickListeners() {
        binding.fob.setOnClickListener {
            addStudent()
        }
    }

    private fun addStudent() {
        val intent = Intent(MainActivity2.activityContext!!, AddStudentFragment::class.java)
        startActivity(intent)
    }


    private fun fetchAllStudentFirst(onComplete: (QuerySnapshot) -> Unit) {
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
            onComplete(it)
        }
    }

    private fun setupSearchBar() {
        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->

            if (!newQuery.isBlank()) {
                mSearchView.showProgress()
                getStudentsAccordingToQuery(newQuery) {


                    val results = filteredQuerySnapshot.map {
                        it.toObject(Student::class.java)
                    }


                    mSearchView.swapSuggestions(results)
                    mSearchView.hideProgress()
                }

            }
        }




        mSearchView.setOnSearchListener(object : OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                val student = searchSuggestion as Student

                onStudentClicked(student)

                Log.d(TAG, "onSuggestionClicked()")

            }

            override fun onSearchAction(query: String) {
                Log.d(TAG, "onSearchAction: ")
                //when keyboard search key is pressed
            }
        })
        mSearchView.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocus() {
                mSearchView.showProgress()
                getSuggestions(3) {
                    mSearchView.swapSuggestions(it.toObjects(Student::class.java))

                    mSearchView.hideProgress()

                }

            }

            override fun onFocusCleared() {

                //  mSearchView.setSearchBarTitle("search bar title")

            }
        })


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(OnMenuItemClickListener { item ->
            if (item.itemId === R.id.dummyItem) {
                mIsDarkSearchTheme = true

                //demonstrate setting colors for items
                mSearchView.setBackgroundColor(Color.parseColor("#787878"))
                mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"))
                mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"))
                mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"))
                mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"))
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"))
                mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"))
                mSearchView.setDividerColor(Color.parseColor("#BEBEBE"))
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"))
            } else {

                //just print action
                Toast.makeText(MainActivity2.activityContext!!, item.title,
                        Toast.LENGTH_SHORT).show()
            }
        })

        mSearchView.setOnHomeActionClickListener(OnHomeActionClickListener { Log.d(TAG, "onHomeClicked()") })


        /* mSearchView.setOnBindSuggestionCallback(OnBindSuggestionCallback { suggestionView, leftIcon, textView, item, itemPosition ->
             val student = item as Student
             val textColor = if (mIsDarkSearchTheme) "#ffffff" else "#000000"
             val textLight = if (mIsDarkSearchTheme) "#bfbfbf" else "#787878"

             *//*   leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,
                       R.drawable.ic_history_black_24dp, null))
               Util.setIconColor(leftIcon, Color.parseColor(textColor))
               leftIcon.alpha = .36f*//*

            leftIcon.alpha = 0.0f
            leftIcon.setImageDrawable(null)

            textView.setTextColor(Color.parseColor(textColor))
            val text: String = student.getBody()
                    .replaceFirst(mSearchView.getQuery(),
                            "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>")
            textView.text = Html.fromHtml(text)
        })*/
    }

    private fun onStudentClicked(student: Student) {

        if (!mainQuerySnapshot.isEmpty) {
            Log.d(TAG, "onStudentClicked: Student id ${student.id}")
            var currentSnapshot: DocumentSnapshot = mainQuerySnapshot.documents[0]
            for (snapshot in mainQuerySnapshot) {
                if (snapshot.id.equals(student.id)) {
                    currentSnapshot = snapshot
                }

            }

            studentDocumentSnapshot = currentSnapshot

            MainActivity2.activityContext!!.supportFragmentManager.beginTransaction().replace(R.id.container, BeginAssessmentFragment()).addToBackStack(null).commit()

        }


    }

    private fun getStudentsAccordingToQuery(newQuery: String, onComplete: () -> Unit) {


        object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                filteredQuerySnapshot.clear()
                val filterResults = FilterResults()
                for (snapshot in mainQuerySnapshot) {
                    val studentFullName = snapshot.toObject(Student::class.java).firstname + " " + snapshot.toObject(Student::class.java).lastname
                    if (studentFullName.contains(newQuery, ignoreCase = true)) {

                        filteredQuerySnapshot.add(snapshot)
                    }

                }


                filterResults.values = filteredQuerySnapshot


                return filterResults

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                onComplete()
            }
        }.filter(newQuery)


    }


    private fun getSuggestions(number: Int, onComplete: (QuerySnapshot) -> Unit) {
        val sharedPreferences = MainActivity2.activityContext!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(MainActivity2.activityContext!!, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            MainActivity2.activityContext!!.supportFragmentManager.popBackStackImmediate()
        }

        FirebaseUtils.getCollectionStudentFromCamp_ReturnCollection(programId, groupId, campId).orderBy("timestamp", Query.Direction.DESCENDING).limit(number.toLong()).get().addOnSuccessListener {
            onComplete(it)
        }
    }


    fun onActivityBackPress(): Boolean {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        return if (!mSearchView.setSearchFocused(false)) {
            false
        } else true
    }

    private fun setupDrawer() {
        binding.mSearchView.attachNavigationDrawerToMenuButton(MainActivity2.activityContext!!.binding.drawerLayout)
        //   attachSearchViewActivityDrawer(mSearchView)
    }

}


