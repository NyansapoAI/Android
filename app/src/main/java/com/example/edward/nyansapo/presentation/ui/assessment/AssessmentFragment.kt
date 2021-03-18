package com.example.edward.nyansapo.presentation.ui.assessment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView.*
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter.OnBindSuggestionCallback
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.FragmentAssessmentBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import es.dmoral.toasty.Toasty


class AssessmentFragment : Fragment(R.layout.fragment_assessment) {

    private val TAG = "AssessmentFragment"


    lateinit var binding: FragmentAssessmentBinding

    val FIND_SUGGESTION_SIMULATED_DELAY: Long = 250
    lateinit var mSearchView: FloatingSearchView


    private var mIsDarkSearchTheme = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssessmentBinding.bind(view)
        mSearchView = view.findViewById(R.id.mSearchView) as FloatingSearchView


        setupDrawer()
        setupSearchBar()
    }

    private fun setupSearchBar() {
        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            } else {

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                mSearchView.showProgress()

                //simulates a query call to a data source
                //with a new query.
                getSuggestions(5) {

                    //this will swap the data and
                    //render the collapse/expand animations as necessary
                    mSearchView.swapSuggestions(it.toObjects(Student::class.java))

                    //let the users know that the background
                    //process has completed
                    mSearchView.hideProgress()
                }
            }
        }



        mSearchView.setOnSearchListener(object : OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                val student = searchSuggestion as Student

                Log.d(TAG, "onSuggestionClicked()")

            }

            override fun onSearchAction(query: String) {
                Log.d(TAG, "onSearchAction: ")
            }
        })
        mSearchView.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                getSuggestions(3) {
                    mSearchView.swapSuggestions(it.toObjects(Student::class.java))
                    Log.d(TAG, "onFocus()")
                }

            }

            override fun onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle("search bar title")

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());
                Log.d(TAG, "onFocusCleared()")
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
                Toast.makeText(requireContext(), item.title,
                        Toast.LENGTH_SHORT).show()
            }
        })

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(OnHomeActionClickListener { Log.d(TAG, "onHomeClicked()") })

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */mSearchView.setOnBindSuggestionCallback(OnBindSuggestionCallback { suggestionView, leftIcon, textView, item, itemPosition ->
            val student = item as Student
            val textColor = if (mIsDarkSearchTheme) "#ffffff" else "#000000"
            val textLight = if (mIsDarkSearchTheme) "#bfbfbf" else "#787878"

            /*   leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,
                       R.drawable.ic_history_black_24dp, null))
               Util.setIconColor(leftIcon, Color.parseColor(textColor))
               leftIcon.alpha = .36f*/

            leftIcon.alpha = 0.0f
            leftIcon.setImageDrawable(null)

            textView.setTextColor(Color.parseColor(textColor))
            val text: String = student.getBody()
                    .replaceFirst(mSearchView.getQuery(),
                            "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>")
            textView.text = Html.fromHtml(text)
        })
    }


    private fun getSuggestions(number: Int, onComplete: (QuerySnapshot) -> Unit) {
        val sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(requireActivity(), "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStackImmediate()
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
        //   attachSearchViewActivityDrawer(mSearchView)
    }

}


