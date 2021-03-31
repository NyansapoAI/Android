package com.nyansapo


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.nyansapo.presentation.utils.FirebaseUtils
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_cumulative_progress.*
import java.util.*

class cumulativeProgress : AppCompatActivity() {
     var missed_words: TextView? = null
    var num_letters = 0
    var num_words = 0
    var num_paragraph = 0
    var num_story = 0
    var students: List<Student>? = null
    var list_letters: ArrayList<Student>? = null
    var list_words: ArrayList<Student>? = null
    var list_paragraph: ArrayList<Student>? = null
    var list_story: ArrayList<Student>? = null
    var assessments: ArrayList<Assessment>? = null
    var dataBaseHandler: dataBaseHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cumulative_progress)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            val myIntent = Intent(baseContext, home::class.java)
            startActivity(myIntent)
        }
        val intent = this.intent
         missed_words = findViewById(R.id.missed_words)
        dataBaseHandler = dataBaseHandler(this)
        students = ArrayList()
        list_letters = ArrayList()
        list_words = ArrayList()
        list_paragraph = ArrayList()
        list_story = ArrayList()
        assessments = ArrayList()


        getStudents{list ->
           students=list


            sortStudents(students)
            letters.setText(Integer.toString(list_letters!!.size))
            words.setText(Integer.toString(list_words!!.size))
            paragraph.setText(Integer.toString(list_paragraph!!.size))
            story.setText(Integer.toString(list_story!!.size))
            total.setText(Integer.toString((students as ArrayList<Student>).size))
            val graph = findViewById<View>(R.id.cumulative_graph) as GraphView
            val series = BarGraphSeries(arrayOf<DataPoint>(
                    DataPoint(1.toDouble(), list_letters!!.size.toDouble()),
                    DataPoint(2.toDouble(), list_words!!.size.toDouble()),
                    DataPoint(3.toDouble(), list_paragraph!!.size.toDouble()),
                    DataPoint(4.toDouble(), list_story!!.size.toDouble())
                    //DataPoint(5.toDouble(), (students as ArrayList<Student>).size.toDouble())
            ))
            series.isAnimated = true
            graph.addSeries(series)
            graph.title = "Students Vs. Literacy Level"
            graph.viewport.isXAxisBoundsManual = true
            graph.viewport.setMinX(0.5)
            graph.viewport.setMaxX(4.5)
            graph.viewport.isYAxisBoundsManual = true
            graph.viewport.setMinY(0.0)
            graph.viewport.setMaxY((students as ArrayList<Student>).size.toDouble())
            graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    return if (isValueX) {
                        when (value.toInt()) {
                            1 -> "Letter"
                            2 -> "Word"
                            3 -> "Paragraph"
                            4 -> "Story"
                            5 -> "Total"
                            else -> "U"
                        }
                    } else super.formatLabel(value, isValueX)
                }
            }
            setMissedWords()

        }

    }

    private fun getStudents(onComplete:(List<Student>)->Unit) {
      /*  FirebaseUtils.studentsCollection.get().addOnSuccessListener {
            onComplete(it.toObjects(Student::class.java))
        }*/
    }

    fun setMissedWords() {

        val dataBaseHandler=dataBaseHandler(this)
        val my_dict = Hashtable<String, Int>()
        assessments = dataBaseHandler!!.allAssessment as ArrayList<Assessment>?
        var words_wrong = ""
        for (assessment in assessments!!) {
            words_wrong = words_wrong + assessment.wordsWrong + assessment.paragraphWordsWrong
        }

        //Toast.makeText(this, words_wrong, Toast.LENGTH_LONG).show();
        var words_list = arrayOf("")
        words_list = words_wrong.split("[,]".toRegex()).toTypedArray()

        //Toast.makeText(this, words_list.toString(), Toast.LENGTH_LONG).show();
        for (word in words_list) {
           var word_ = word.toLowerCase()
            val count = if (my_dict.containsKey(word_))
                my_dict[word_]!!
            else 0
            my_dict[word_] = count + 1
        }

        //Toast.makeText(this, my_dict.toString(), Toast.LENGTH_LONG).show();

        //my_dict.
        class ValueComparator(var base: Map<String, Int>) : Comparator<String> {
            // Note: this comparator imposes orderings that are inconsistent with
            // equals.
            override fun compare(a: String, b: String): Int {
                return if (base[a]!! >= base[b]!!) {
                    -1
                } else {
                    1
                } // returning 0 would merge keys
            }
        }

        val bvc = ValueComparator(my_dict)
        val sorted_map = TreeMap<String, Int>(bvc)
        sorted_map.putAll(my_dict)
        //Toast.makeText(this, sorted_map.toString(), Toast.LENGTH_LONG).show();
        var sorted_words = ""
        var i = 0
        for (word in sorted_map.keys) {
            if (i < 20) sorted_words = "$sorted_words$word, "
            i++
        }
        missed_words!!.text = sorted_words
    }

    fun sortStudents(students: List<Student>?) {
        val len = students!!.size
        for (i in 0 until len) {
            when (students[i].learningLevel) {
                "LETTER" -> {
                    list_letters!!.add(students[i])
                }
                "WORD" -> {
                    list_words!!.add(students[i])
                }
                "PARAGRAPH" -> {
                    list_paragraph!!.add(students[i])
                }
                "STORY" -> {
                    list_story!!.add(students[i])
                }
            }
        }
    }

    fun gohome(v: View?) {
        val myIntent = Intent(baseContext, home::class.java)
        startActivity(myIntent)
    }

    fun goSettings(v: View?) {
        val myIntent = Intent(baseContext, settings::class.java)
        startActivity(myIntent)
    }
}