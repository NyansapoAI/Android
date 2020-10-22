package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class thankYou extends AppCompatActivity {

    Button done_button;

    dataBaseHandler dataBaseHandler;
    Assessment assessment;

    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        done_button = findViewById(R.id.done_button);

        //Intent intent = getIntent();
        //assessment = intent.getParcelableExtra("Assessment");

        Intent intent = getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment");
        /*
        Toast.makeText(this, assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLEARNING_LEVEL(),Toast.LENGTH_SHORT).show();*/
        dataBaseHandler = new dataBaseHandler(this);

        storeAssessment(); // will do on another thread


        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextAssessment(v);
            }
        });
    }

    public void nextAssessment(View w){
        Intent myIntent = new Intent(getBaseContext(), home.class);
        //Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void storeAssessment(){
        //dataBaseHandler.addAssessment(assessment);
        assessment.setTIMESTAMP(new Date(System.currentTimeMillis()).toString());

        // first update in cloud and if successful update locally
        postAssessment(assessment);
        updateLearning_level(assessment);
        //dataBaseHandler.updateStudentLevel(assessment.getSTUDENT_ID(), assessment.getLEARNING_LEVEL());

    }

    public void updateLearning_level(Assessment assessment){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/student/learning_level";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dataBaseHandler.updateStudentLevel(assessment.getSTUDENT_ID(), assessment.getLEARNING_LEVEL());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dataBaseHandler.updateStudentLevel(assessment.getSTUDENT_ID(), assessment.getLEARNING_LEVEL());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", assessment.getSTUDENT_ID());
                params.put("learning_level", assessment.getLEARNING_LEVEL());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }


    public void postAssessment(Assessment assessment){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/assessment";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(thankYou.this, getId(response), Toast.LENGTH_LONG).show();
                assessment.setCLOUD_ID(getId(response));
                dataBaseHandler.addAssessment(assessment);

                /*
                Toast.makeText(thankYou.this,assessment.getLETTERS_WRONG(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getLETTERS_CORRECT(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getWORDS_WRONG(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getWORDS_CORRECT(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
                Toast.makeText(thankYou.this,assessment.getLEARNING_LEVEL(), Toast.LENGTH_SHORT).show();*/
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(thankYou.this, error.toString() , Toast.LENGTH_LONG).show();
                dataBaseHandler.addAssessment(assessment);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                //params.put("firstname", instructor.getFirstname());
                params.put("student_id", assessment.getSTUDENT_ID());
                //params.put("student_id", "5f0fcc391bf5061ed35f7562");
                params.put("timestamp", assessment.getTIMESTAMP());
                params.put("learning_level", assessment.getLEARNING_LEVEL());
                params.put("assessment_key", assessment.getASSESSMENT_KEY());
                params.put("letters_correct",assessment.getLETTERS_CORRECT());
                params.put("letters_wrong",assessment.getLETTERS_WRONG());
                params.put("words_correct", assessment.getWORDS_CORRECT());
                params.put("words_wrong", assessment.getWORDS_WRONG());
                params.put("paragrahp_words_wrong", assessment.getPARAGRAPH_WORDS_WRONG());
                params.put("story_ans_q1", assessment.getSTORY_ANS_Q1());
                params.put("story_ans_q2", assessment.getSTORY_ANS_Q2());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public String getId(String response){
        String id = response.split(":")[1];
        id = id.replace("}", ""); // remove }
        id = id.replace("\"", ""); // remove "
        return id;
    }

}
