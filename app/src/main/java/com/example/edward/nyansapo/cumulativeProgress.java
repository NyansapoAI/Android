package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class cumulativeProgress extends AppCompatActivity {

    String instructor_id;

    TextView letters;
    TextView words;
    TextView paragraph;
    TextView story;
    TextView total;

    int num_letters;
    int num_words;
    int num_paragraph;
    int num_story;

    ArrayList<Student> students;
    ArrayList<Student> list_letters;
    ArrayList<Student> list_words;
    ArrayList<Student> list_paragraph;
    ArrayList<Student> list_story;

    dataBaseHandler dataBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cumulative_progress);

        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");

        letters = findViewById(R.id.letters);
        words = findViewById(R.id.words);
        paragraph = findViewById(R.id.paragraph);
        story = findViewById(R.id.story);
        total  = findViewById(R.id.total);

        dataBaseHandler = new dataBaseHandler(this);
        students = new ArrayList<Student>();
        list_letters = new ArrayList<Student>();
        list_words = new ArrayList<Student>();
        list_paragraph = new ArrayList<Student>();
        list_story = new ArrayList<Student>();

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