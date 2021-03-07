package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File
import java.util.*


class home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AddDialog.AddDialogListener {


    lateinit var adapter: HomeAdapter


    // Gesture code
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    private val gestureDetector: GestureDetector? = null
    var v: View? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
        //return super.onCreateOptionsMenu(menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_student -> {
                val myIntent = Intent(baseContext, registerStudent::class.java)
                startActivity(myIntent)
                //Toast.makeText(this, "Add Selected", Toast.LENGTH_LONG).show();
                true
            }
            R.id.sync ->                 // Toast.makeText(this, "Search Selected", Toast.LENGTH_LONG).show();
                true
            R.id.analytics -> {
                val myIntent2 = Intent(baseContext, cumulativeProgress::class.java)
                startActivity(myIntent2, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                //Toast.makeText(this, "Attendace Selected", Toast.LENGTH_LONG).show();
                true
            }
            R.id.paragraph, R.id.story -> {
                //case R.id.male:
                //case R.id.female:
                //case R.id.name:
                //case R.id.level:
                Toast.makeText(this, "Under Development", Toast.LENGTH_LONG).show()
                true
            }
            R.id.settings -> {
                val myIntent1 = Intent(baseContext, settings::class.java)
                startActivity(myIntent1, ActivityOptions.makeSceneTransitionAnimation(this@home).toBundle())
                //Toast.makeText(this, "female Selected", Toast.LENGTH_LONG).show();
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
         val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        // get intent values
        val intent = this.intent
        //Token = intent.getStringExtra("Token");
        //Toast.makeText(getApplicationContext(), instructor_id , Toast.LENGTH_SHORT).show();

        // code for student_activity view
        // connect to xml
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        bt_add.setOnClickListener(View.OnClickListener { v -> addstudent(v) })
        initProgressBar()
        initRecyclerViewAdapter()
        setSwipeListenerForItems()

    }

    private fun initRecyclerViewAdapter() {
        val query: Query = FirebaseUtils.studentsCollection
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java)
                .setLifecycleOwner(this).build()


        adapter = HomeAdapter(this, firestoreRecyclerOptions, { onStudentClicked() })
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

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.share -> {
                Toast.makeText(this, "Share App is under development", Toast.LENGTH_LONG).show()
            }
            R.id.send -> {

                //Toast.makeText(this, "Send data", Toast.LENGTH_LONG).show();
                exportData()
            }
        }
        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
    }

    fun exportApp() {
        try {
            val ShareAppIntent = Intent(Intent.ACTION_SEND)
            ShareAppIntent.type = "text/plain"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exportData() {

        val students=adapter.snapshots.toList()
        val data = StringBuilder()
        data.append("Firstname,Lastname,Age,Gender,Class,Learning_Level") // generate headers
        for (student in students!!) { // generate csv data
            data.append("""
    
    ${student.getFirstname()},${student.getLastname()},${student.getAge()},${student.getGender()},${student.getStd_class()},${student.getLearning_level()}
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

    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    fun openDialog() {
        val addDialog = AddDialog()
        addDialog.setInfo("Add Student", "Do you want to add a student?")
        addDialog.show(supportFragmentManager, "Add student")
    }

    fun addstudent(v: View?) {
        val myIntent = Intent(baseContext, registerStudent::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun cumulativeProgress(v: View?) {
        val myIntent = Intent(baseContext, cumulativeProgress::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun onStudentClicked() {
        //students.get(position);
        val intent = Intent(this@home, student_assessments::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    /// Gesture code
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector!!.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                y2 = event.y
                val y_value = y2 - y1
                if (Math.abs(y_value) > 50) {
                    if (y2 > y1) {
                        // swipe down
                        Toast.makeText(this, "Update up", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onYesClicked() {
        val myIntent = Intent(baseContext, registerStudent::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
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

    companion object {
        private const val MIN_DIST = 150
    }
}