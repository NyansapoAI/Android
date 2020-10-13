package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
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
        startActivity(intent);
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

        String url = "https://nyansapoai-api.azurewebsites.net/student/ofInstructor";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //String url = "https://localhost:3000/student/register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

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