package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.View
import android.view.ViewGroup.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.SelectAssessmentModal.AssessmentModalListener
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class student_assessments : AppCompatActivity(), AssessmentModalListener, AddDialog.AddDialogListener {

    lateinit var adapter: StudentAssessmentAdapter
    var arrayList: ArrayList<*>? = null
    var arrayAdapter: ArrayAdapter<*>? = null
    var assessments: ArrayList<Assessment>? = null
    var student: Student? = null
    var btAdd: FloatingActionButton? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.student_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val myIntent = Intent(baseContext, studentSettings::class.java)
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@student_assessments).toBundle())
                true
            }
            R.id.add_assessment -> {
                addAssessment()
                true
            }
            R.id.analytics -> {
                val intent = Intent(this@student_assessments, studentDetails::class.java)
                 startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_assessments)

        initProgressBar()

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { //startActivity(new Intent(getApplicationContext(), home.class));
            val myIntent = Intent(baseContext, home::class.java)
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@student_assessments).toBundle())
        }

        // get student_activity
        val intent = intent
        student = intent.getParcelableExtra("student_activity")
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        btAdd = findViewById(R.id.bt_add)


        // Initialize DatabaseHelper
        btAdd = findViewById(R.id.bt_add)
        btAdd!!.setOnClickListener(View.OnClickListener { addAssessment() })
        assessments = ArrayList()
        initRecyclerViewAdapter()
        setSwipeListenerForItems()

 // populate students ArrayList
        //Toast.makeText(this,assessments.toString(), Toast.LENGTH_LONG).show();

        /*Assessment assessment =  assessments.get(0);
        Toast.makeText(this,assessment.getLETTERS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLETTERS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLEARNING_LEVEL(), Toast.LENGTH_SHORT).show();*/
    }

    private fun initRecyclerViewAdapter() {
        val query: Query = FirebaseUtils.assessmentsCollection
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Assessment>().setQuery(query, Assessment::class.java)
                .setLifecycleOwner(this).build()


        adapter = StudentAssessmentAdapter(this, firestoreRecyclerOptions, {
            onAssmentClicked(it) })
        recyclerview.setLayoutManager(LinearLayoutManager(this))
        recyclerview.setAdapter(adapter)

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


    fun openDialog() {
        val addDialog = AddDialog()
        addDialog.setInfo("Add Assessment", "Do you want to add an Assessment?")
        addDialog.show(supportFragmentManager, "Add Assessment")
    }

    private fun addAssessment() {
        /*
        Intent intent = new Intent(student_assessments.this, SelectAssessment.class);
        intent.putExtra("instructor_id", instructor_id);
        intent.putExtra("student_id",student_activity.getLocal_id());
        intent.putExtra("student_activity", student_activity);
        //intent.putExtra("assessment", assessments.get(position));
        startActivity(intent);

         */
        val selectAssessmentModal = SelectAssessmentModal()
        selectAssessmentModal.show(supportFragmentManager, "Select Assessment Modal")
    }

     fun onAssmentClicked(assessment: Assessment) {
        //students.get(position);
        val intent = Intent(this@student_assessments, assessment_detail::class.java)
        intent.putExtra("assessment", assessment)
        startActivity(intent)
    }


    override fun onButtonClicked(text: String) {

        //Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
        when (text) {
            "assessment_3" -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("ASSESSMENT_KEY", "3")
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            "assessment_4" -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("ASSESSMENT_KEY", "4")
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            "assessment_5" -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("ASSESSMENT_KEY", "5")
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            else -> {
            }
        }
    }

    override fun onYesClicked() {
        addAssessment()
    }


    /////////////////////PROGRESS_BAR////////////////////////////
    lateinit var dialog: AlertDialog

     fun showProgress(show: Boolean) {

        if (show) {
            dialog.show()

        } else {
            dialog.dismiss()

        }

    }

    private fun initProgressBar() {

        dialog = setProgressDialog(this, "Loading..")
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