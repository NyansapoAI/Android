package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.DatabaseMetaData;
import java.sql.SQLDataException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

public class studentDetails extends AppCompatActivity  implements  View.OnClickListener {

    // declare ui elements
    GraphView graphView;
    TextView student_name;
    TextView initial_level;
    TextView current_level;
    TextView assessments_taken;

    // buttons
    Button settings_button;
    Button assessment_button;
    Button home_button;

    // database
    dataBaseHandler DataBaseHandler;
    ArrayList<Assessment> assessmentList;
    String student_id;
    Student student;
    ArrayList students;

    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_student_details);

            Intent intent = getIntent();
            student = intent.getParcelableExtra("student_activity");
            instructor_id = intent.getStringExtra("instructor_id");
            //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();


            // toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(studentDetails.this, student_assessments.class);
                    intent.putExtra("instructor_id", instructor_id);
                    intent.putExtra("student_activity", student);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(studentDetails.this).toBundle());

                }
            });



            DataBaseHandler = new dataBaseHandler(this);
            assessmentList = new ArrayList<Assessment>();
            //assessmentList = DataBaseHandler.getAllAssessment();
            assessmentList = DataBaseHandler.getAllStudentAssessment(student.getCloud_id());
            //Toast.makeText(this,Integer.toString(assessmentList.size()), Toast.LENGTH_LONG ).show();

            //Assessment assessment =  assessmentList.get(0);
            /*
            Toast.makeText(this,assessment.getLETTERS_WRONG(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getLETTERS_CORRECT(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getWORDS_WRONG(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getWORDS_CORRECT(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,assessment.getLEARNING_LEVEL(), Toast.LENGTH_SHORT).show();*/

            // assign ui elements
            student_name = findViewById(R.id.student_name);
            initial_level = findViewById(R.id.initial_level);
            current_level = findViewById(R.id.current_level);
            assessments_taken = findViewById(R.id.assessments_taken);

            assessments_taken.setText("0"); // updated
            student_name.setText(student.getFirstname() + ' ' + student.getLastname());

            // code for buttons
            //settings_button = findViewById(R.id.settings_button);
            assessment_button = findViewById(R.id.new_assessment);
            home_button = findViewById(R.id.home_button);


            // set onclick listeners
            assessment_button.setOnClickListener(this);
            home_button.setOnClickListener(this);

            if(assessmentList.size() > 0){
                //Toast.makeText(this, assessmentList.get(assessmentList.size()-1).getLEARNING_LEVEL(),Toast.LENGTH_LONG).show();

                graphView = (GraphView) findViewById(R.id.graphview);
                LineGraphSeries<DataPoint>series = new LineGraphSeries<DataPoint>();

                int num = assessmentList.size();
                assessments_taken.setText(Integer.toString(num));
                for (int i =0; i < num && i < 5 ; i++){
                    series.appendData(new DataPoint(i+1,getLevelIndex(assessmentList.get(i).getLEARNING_LEVEL() )), true, 5);
                }

                series.setAnimated(true);
                graphView.addSeries(series);
                graphView.setTitle("Literacy Level Vs. Time of Current Assessments");
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinX(1);
                graphView.getViewport().setMaxX(5);
                graphView.getViewport().setYAxisBoundsManual(true);
                graphView.getViewport().setMinY(0);
                graphView.getViewport().setMaxY(4);

                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(!isValueX){
                            switch ((int) value){
                                case 0: return "L";
                                case 1: return "W";
                                case 2: return "P";
                                case 3: return "S";
                                case 4: return "A";
                                default: return "U";
                            }
                        }
                        return super.formatLabel(value, isValueX);


                    }
                });
                current_level.setText(assessmentList.get(num-1).getLEARNING_LEVEL());
                initial_level.setText(assessmentList.get(0).getLEARNING_LEVEL());

            } else {

                    graphView = (GraphView) findViewById(R.id.graphview);
                /*
                LineGraphSeries<DataPoint>series = new LineGraphSeries<DataPoint>();
                series.appendData(new DataPoint(1,1), true, 10); // add datapoints
                series.appendData(new DataPoint(2,3), true, 10);
                series.appendData(new DataPoint(3,3), true, 10);
                series.setAnimated(true); // animate datapoints */

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                            new DataPoint(0, 0),
                            new DataPoint(1, 0),
                            new DataPoint(2, 0),
                            new DataPoint(3, 0),
                            new DataPoint(4, 0),
                            //new DataPoint(5, 5)
                    });

                    series.setAnimated(true); // set animation
                    graphView.addSeries(series);
                    graphView.setTitle("No Assessment has been recorded");

                    graphView.getViewport().setXAxisBoundsManual(true);
                    graphView.getViewport().setMinX(1);
                    graphView.getViewport().setMaxX(5);
                    graphView.getViewport().setYAxisBoundsManual(true);
                    graphView.getViewport().setMinY(0);
                    graphView.getViewport().setMaxY(4);

                    graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if(!isValueX){
                                switch ((int) value){
                                    case 0: return "L";
                                    case 1: return "W";
                                    case 2: return "P";
                                    case 3: return "S";
                                    case 4: return "A";
                                    default: return "U";
                                }
                            }
                            return super.formatLabel(value, isValueX);


                        }
                    });


                    // set ui elements with student_activity data

                    current_level.setText("UKN");
                    initial_level.setText("Unkown");


            }

    }

    public int getLevelIndex(String level){
        switch (level){
            case "LETTER": return 0;
            case "WORD" : return 1;
            case "PARAGRAPH" : return 2;
            case "STORY" : return 3;
            case "ABOVE": return 4;
            default: return -1;
        }
    }

    public void gohome(View v){ // function to handle home button
        Intent myIntent = new Intent(getBaseContext(), student_assessments.class);
        myIntent.putExtra("instructor_id", instructor_id);
        myIntent.putExtra("student_activity", student);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goSettings(View v){ // function to handle settings button
        Intent myIntent = new Intent(getBaseContext(), settings.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goAssessment(View v){ // function to handle new assessment button
        Intent myIntent = new Intent(getBaseContext(), SelectAssessment.class);
        myIntent.putExtra("student_activity", student);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onClick(View v) { // assign function for the onclick listener buttons
            switch (v.getId()){
                case R.id.home_button:
                    gohome(v);
                    break;
                case R.id.new_assessment:
                    goAssessment(v);
                    break;

            }
    }
}