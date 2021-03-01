package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class cumulativeProgress extends AppCompatActivity {

    String instructor_id;

    TextView letters;
    TextView words;
    TextView paragraph;
    TextView story;
    TextView total;

    TextView missed_words;

    int num_letters;
    int num_words;
    int num_paragraph;
    int num_story;

    ArrayList<Student> students;
    ArrayList<Student> list_letters;
    ArrayList<Student> list_words;
    ArrayList<Student> list_paragraph;
    ArrayList<Student> list_story;

    ArrayList<Assessment> assessments;

    dataBaseHandler dataBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cumulative_progress);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(cumulativeProgress.this).toBundle());
            }
        });

        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");

        letters = findViewById(R.id.letters);
        words = findViewById(R.id.words);
        paragraph = findViewById(R.id.paragraph);
        story = findViewById(R.id.story);
        total  = findViewById(R.id.total);
        missed_words = findViewById(R.id.missed_words);

        dataBaseHandler = new dataBaseHandler(this);
        students = new ArrayList<Student>();
        list_letters = new ArrayList<Student>();
        list_words = new ArrayList<Student>();
        list_paragraph = new ArrayList<Student>();
        list_story = new ArrayList<Student>();

        assessments = new ArrayList<Assessment>();

        students = dataBaseHandler.getAllStudentOfInstructor(instructor_id);

        sortStudents(students);

        letters.setText(Integer.toString(list_letters.size()));
        words.setText(Integer.toString(list_words.size()));
        paragraph.setText(Integer.toString(list_paragraph.size()));
        story.setText(Integer.toString(list_story.size()));
        total.setText(Integer.toString(students.size()));





        GraphView graph = (GraphView) findViewById(R.id.cumulative_graph);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, list_letters.size()),
                new DataPoint(2, list_words.size()),
                new DataPoint(3, list_paragraph.size()),
                new DataPoint(4, list_story.size()),
                new DataPoint(5, students.size()),


        });
        series.setAnimated(true);
        graph.addSeries(series);
        graph.setTitle("Students Vs. Literacy Level");

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(students.size());

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    switch ((int) value){
                        case 1: return "Letter";
                        case 2: return "Word";
                        case 3: return "Paragraph";
                        case 4: return "Story";
                        case 5: return "Total";
                        default: return "U";
                    }
                }
                return super.formatLabel(value, isValueX);


            }
        });

        setMissedWords();

    }

    public void setMissedWords(){
        Hashtable<String, Integer> my_dict = new Hashtable<String, Integer>();
        assessments = dataBaseHandler.getAllAssessment();

        String words_wrong ="";

        for (Assessment assessment : assessments){
             words_wrong = words_wrong + assessment.getWORDS_WRONG() + assessment.getPARAGRAPH_WORDS_WRONG();
        }

        //Toast.makeText(this, words_wrong, Toast.LENGTH_LONG).show();

        String[] words_list = {""};
        words_list = words_wrong.split("[,]");

        //Toast.makeText(this, words_list.toString(), Toast.LENGTH_LONG).show();

        for (String word: words_list){
            word = word.toLowerCase();
            int count = my_dict.containsKey(word) ? my_dict.get(word) : 0;
            my_dict.put(word, count +1);
        }

        //Toast.makeText(this, my_dict.toString(), Toast.LENGTH_LONG).show();

        //my_dict.



        class ValueComparator implements Comparator<String> {
            Map<String, Integer> base;

            public ValueComparator(Map<String, Integer> base) {
                this.base = base;
            }

            // Note: this comparator imposes orderings that are inconsistent with
            // equals.
            public int compare(String a, String b) {
                if (base.get(a) >= base.get(b)) {
                    return -1;
                } else {
                    return 1;
                } // returning 0 would merge keys
            }
        }


        ValueComparator bvc = new ValueComparator(my_dict);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(my_dict);
        //Toast.makeText(this, sorted_map.toString(), Toast.LENGTH_LONG).show();

        String sorted_words = "";

        int i=0;
        for (String word: sorted_map.keySet()){
            if(i < 20) sorted_words = sorted_words + word + ", ";
            i++;
        }

        missed_words.setText(sorted_words);

    }

    public void sortStudents(ArrayList<Student> students){
        int len = students.size();
        for( int i=0; i < len; i++){
            switch (students.get(i).getLearning_level()){
                case "LETTER": {
                    list_letters.add(students.get(i));
                    break;
                }
                case "WORD": {
                    list_words.add(students.get(i));
                    break;
                }
                case "PARAGRAPH": {
                    list_paragraph.add(students.get(i));
                    break;
                }
                case "STORY": {
                    list_story.add(students.get(i));
                    break;
                }
            }
        }
    }

    public void gohome(View v){
        Intent myIntent = new Intent(getBaseContext(), home.class);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goSettings(View v){
        Intent myIntent = new Intent(getBaseContext(), settings.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}