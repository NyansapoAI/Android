package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterTeacher extends AppCompatActivity {

    // declare views
    EditText firstname;
    EditText lastname;
    EditText email;
    EditText password1;
    EditText password2;

    // progress_bar
    int network_lock;
    Button register;
    loading_progressBar progressBar;
    // declare database connection
    dataBaseHandler databasehelper;

    // declare Instructor object
    Instructor instructor;

    // Control vall
    int success = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);

        // Initialize views
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);

        register = findViewById(R.id.register_teacher);

        // Initialize DatabaseHelper
        databasehelper = new dataBaseHandler(RegisterTeacher.this); // might need to try catch

        // initialize object
        instructor = new Instructor();

        // progress bar
        network_lock = 0;
        progressBar = new loading_progressBar(RegisterTeacher.this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(network_lock == 0){
                    startSelector(view);
                }else{
                    Toast.makeText(RegisterTeacher.this, "Registering Instructor Wait ...", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void startSelector(View v){
        // need to do input validation later
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();

        if(pass1.compareTo(pass2) == 0){ // password validated

            network_lock = 1;
            progressBar.showDialog();

            // populate instructor object
            instructor.setEmail(email.getText().toString());
            instructor.setFirstname(firstname.getText().toString());
            instructor.setLastname(lastname.getText().toString());
            instructor.setPassword(pass1);
            instructor.setTimestamp(new Date(System.currentTimeMillis()).toString());

            // save in database
            postTeacher(instructor);

            //Intent myIntent = new Intent(getBaseContext(), home.class);
            //startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        }else{
            Toast.makeText(this, "Password Mismatch", Toast.LENGTH_LONG).show();
        }
    }

    public void postTeacher(Instructor instructor){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/instructor/signup";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                network_lock = 0;
                progressBar.dismissDialog();
                //Toast.makeText(RegisterTeacher.this, getId(response), Toast.LENGTH_LONG).show();
                instructor.setCloud_id(getId(response)); // set cloud id
                instructor.setLocal_id(UUID.randomUUID().toString());  // set local id
                //Toast.makeText(RegisterTeacher.this, instructor.getCloud_id(), Toast.LENGTH_LONG).show();
                databasehelper.addTeacher(instructor);

                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", getId(response)); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(RegisterTeacher.this).toBundle());

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                network_lock = 0;
                progressBar.dismissDialog();
                String responseBody = null;
                try {
                    responseBody = new String(error.networkResponse.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                JSONObject data = null;
                try {
                    data = new JSONObject(responseBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = data.optString("message");

                Toast.makeText(RegisterTeacher.this, message , Toast.LENGTH_LONG).show();
                //databasehelper.addTeacher(instructor);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", instructor.getFirstname());
                params.put("lastname", instructor.getLastname());
                params.put("email", instructor.getEmail());
                params.put("password", instructor.getPassword());
                params.put("timestamp", instructor.getTimestamp());
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
        success = 1;
        String id = response.split(":")[1];
        id = id.replace("}", ""); // remove }
        id = id.replace("\"", ""); // remove "
        return id;
    }

}
