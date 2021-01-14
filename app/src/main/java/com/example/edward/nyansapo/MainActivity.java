package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edward.nyansapo.SpeechRecognition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity  {

    // declare view
    EditText user_email;
    EditText user_password;

    // declare database connection
    dataBaseHandler databasehelper;

    // declare Instructor object
    Instructor instructor;

    String Token= "";

    Animation loginAnimation;
    ImageView rotating_icon;

    Button signin;
    Button signup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_email = findViewById(R.id.email);
        user_password = findViewById(R.id.password);

        // Initialize DatabaseHelper
        databasehelper = new dataBaseHandler(MainActivity.this); // might need to try catch

        //checkUser();
        //setContentView(R.layout.activity_main);

        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        loginAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        loginAnimation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        rotating_icon = findViewById(R.id.rotating_icon);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);



    }


    public void startSelector(View v){

        login(user_email.getText().toString(), user_password.getText().toString());
        //login("edward@kijenzi.com", "nyansapo");
        rotating_icon.setVisibility(View.VISIBLE);
        rotating_icon.startAnimation(loginAnimation);
        //email.setEnabled(false);
        //password.setEnabled(false);
        signin.setEnabled(false);
        signup.setEnabled(false);

    }


    public void registerTeacher(View v){
        Intent myIntent = new Intent(getBaseContext(), RegisterTeacher.class);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void nextAssessment(View view) {
    }

    public void login(String email, String password){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/instructor/signin";
        //String url = "https://localhost:3000/student/register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this, getToken(response), Toast.LENGTH_LONG).show();
                Token = getToken(response);

                String v =   databasehelper.addUser(email,Token,Token,"1"); // Cache User

                // save instructor in local database
                Instructor instructor = new Instructor();
                instructor.setCloud_id(Token);
                instructor.setEmail(email);
                instructor.setFirstname("");
                instructor.setLastname("");
                databasehelper.addTeacher(instructor);
                

                /*if(v = true){
                    Toast.makeText(MainActivity.this, "User is cached", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, databasehelper.getUserToken(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, databasehelper.getUserID(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, databasehelper.(), Toast.LENGTH_SHORT).show();
                }*/

                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", Token); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());

                rotating_icon.clearAnimation();
                rotating_icon.setAnimation(null);
                rotating_icon.setVisibility(View.GONE);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String responseBody = null;
                try {
                    responseBody = new String(error.networkResponse.data, "utf-8");
                } catch (Exception e) {  //UnsupportedEncoding
                    e.printStackTrace();
                }
                JSONObject data = null;
                try {
                    data = new JSONObject(responseBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = data.optString("message");

                Toast.makeText(MainActivity.this, message , Toast.LENGTH_LONG).show();
                //databasehelper.addStudent(student_activity);
                signin.setEnabled(true);
                signup.setEnabled(true);
                //rotating_icon.startAnimation();
                rotating_icon.clearAnimation();
                rotating_icon.setAnimation(null);
                rotating_icon.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                //Toast.makeText(MainActivity.this, email, Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this, password, Toast.LENGTH_LONG).show();
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

    public String getToken(String response){
        String token = response.split(":")[1];
        token = token.replace("}", ""); // remove }
        token = token.replace("\"", ""); // remove "
        return token;
    }





}
