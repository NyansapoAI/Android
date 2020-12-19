package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home extends AppCompatActivity implements CustomViewAdapter.OnStudentListener, GestureDetector.OnGestureListener {

    // Initialize Variables
    EditText etText;
    FloatingActionButton btAdd;
    ListView listView;
    Button update_button, account;


    // database helper
    dataBaseHandler databasehelper;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;


    // student code
    ArrayList<Student> students;

    // Instructor id
    String instructor_id;


    // Gesture code
    private float x1,x2,y1,y2;
    private static int MIN_DIST = 150;
    private GestureDetector gestureDetector;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get intent values
        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Token = intent.getStringExtra("Token");
        //Toast.makeText(getApplicationContext(), instructor_id , Toast.LENGTH_SHORT).show();

        // code for student view
        // connect to xml
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        account = findViewById(R.id.account_settings);
        update_button = findViewById(R.id.update_data);

        // Initialize DatabaseHelper
        databasehelper = new dataBaseHandler(home.this);
        btAdd = findViewById(R.id.bt_add);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addstudent(v);
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync(instructor_id);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(home.this, "Under Development", Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent(getBaseContext(), settings.class);
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(home.this).toBundle());


            }
        });
        students = new ArrayList<Student>();
        getStudents(); // populate students ArrayList

        CustomViewAdapter customViewAdapter = new CustomViewAdapter(home.this, students, this::OnStudentClick);
        recyclerView.setAdapter(customViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(home.this));



        // Gesture initialization
        this.gestureDetector = new GestureDetector(home.this, this);

    }


    void getStudents(){
        if(instructor_id == null || instructor_id == ""){
            instructor_id = "5f39b701b4270100524952ed";
        }

        //students = databasehelper.getAllStudent();
        students = databasehelper.getAllStudentOfInstructor(instructor_id);
    }

    public void addstudent(View v){
        Intent myIntent = new Intent(getBaseContext(), registerStudent.class);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void cumulativeProgress(View v){
        Intent myIntent = new Intent(getBaseContext(), cumulativeProgress.class);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void OnStudentClick(int position) {
        //students.get(position);
        Intent intent = new Intent(home.this, student_assessments.class);
        intent.putExtra("instructor_id", instructor_id);
        intent.putExtra("student", students.get(position));
        //startActivity(intent);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }



    /// Gesture code


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()){
            // start to swipe
            case MotionEvent.ACTION_DOWN:{
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            // end swipe
            case MotionEvent.ACTION_UP:{
                x2 = event.getX();
                y2 = event.getY();

                float y_value = y2-y1;
                if(Math.abs(y_value)> 50){
                    if(y2>y1){
                        // swipe down
                        Toast.makeText(this, "Update up", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    // update code

    public void sync(String instructor_id){

        for (Student student: students) {
            databasehelper.deleteStudent(student.getCloud_id());
        }


        String url = "https://nyansapoai-api.azurewebsites.net/student/ofInstructor";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Toast.makeText(home.this, instructor_id, Toast.LENGTH_SHORT).show();

        // set parameters
        Map<String, String> params = new HashMap();
        params.put("instructor_id", instructor_id);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String names;
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    //Toast.makeText(home.this,jsonArray.toString(),Toast.LENGTH_SHORT).show();
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject student = jsonArray.getJSONObject(i);
                        String cloud_id = student.getString("_id");

                        //Toast.makeText(home.this,  cloud_id +"  "+ student.toString(), Toast.LENGTH_SHORT).show();

                        if(databasehelper.FindStudent(cloud_id) == 0){ // Not in local database
                            // assign values
                            String firstname = student.getString("firstname");
                            String lastname = student.getString("lastname");
                            String age = student.getString("age");
                            String notes = student.getString("notes");
                            String gender = student.getString("gender");
                            String learning_level = student.getString("learning_level");
                            String std_class = student.getString("std_class");
                            String timestamp = student.getString("timestamp");

                            //Toast.makeText(home.this, firstname +" "+ lastname+" "+ age+" "+ notes+" "+learning_level+" "+std_class+" "+timestamp, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(home.this, learning_level, Toast.LENGTH_SHORT).show();
                            // create student instance
                            Student std = new Student();
                            std.setInstructor_id(instructor_id);
                            std.setCloud_id(cloud_id);
                            std.setFirstname(firstname);
                            std.setLastname(lastname);
                            std.setAge(age);
                            std.setNotes(notes);
                            std.setGender(gender);
                            std.setLearning_level(learning_level);
                            std.setStd_class(std_class);
                            std.setTimestamp(timestamp);
                            //Toast.makeText(home.this, std.firstname + " "+std.getLastname() + " "+ std.getCloud_id()+" "+ std.getAge(), Toast.LENGTH_SHORT).show();
                            databasehelper.addStudent(std); // add student to local database
                            //Toast.makeText(home.this, "student saved", Toast.LENGTH_SHORT).show();
                        }


                    }
                    finish();
                    startActivity(getIntent());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        /*

        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("instructor_id", instructor_id);
                Toast.makeText(home.this, instructor_id, Toast.LENGTH_SHORT).show();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        }
         */

        /* stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(home.this, response, Toast.LENGTH_LONG).show();
                    //extractStudent(response);

                }
           }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(home.this, "Error "+ error.toString() , Toast.LENGTH_LONG).show();
                    //databasehelper.addStudent(student);

                }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("instructor_id", instructor_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };*/

        jsonObject.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObject);

    }

    public void extractStudent(String response){
        JSONObject jsonObject = null;
        try {
             jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int len = jsonObject.length();

        //loop to get all json objects from data json array
        for(int i=0; i< 5; i++)
        {
            JSONObject jObj = null;
            try {
                jObj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Toast.makeText(this,
                        jObj.getString("Name"),
                        Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}