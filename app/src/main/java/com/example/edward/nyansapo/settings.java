package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class settings extends AppCompatActivity {

    String instructor_id;

    Button home_button, update_button, download_data;
    TextView firstname, lastname, email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // get intent values
        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");

        // ui code
        home_button = findViewById(R.id.home_button);
        update_button = findViewById(R.id.update_button);
        download_data = findViewById(R.id.download_button);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gohome(v);
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

    }

    public void updateInfo(){

        // check inputs
        assert firstname.getText() == "";
        assert lastname.getText() == "";
        assert email.getText() == "";
        assert password.getText() == "";

        /*
{
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImVkd2FyZEAxa2lqZW56aS5jb20iLCJ1c2VySWQiOiI1Zjg1YWY4YzJkYjM4MzI3N2FkOWU0NTkiLCJpYXQiOjE2MDI2NjgwNTcsImV4cCI6MTYwMjY3MTY1N30.0zijIUm8nllFVhAT41P_8mVGBoh5VsL5xyu2Z9fI_Q8",
            "updates":
                    [
                        {"firstname": "Mumbe"},
                            {"password":"1234"}
                ]
}
         */

    }

    public void gohome(View v){
        Intent myIntent = new Intent(getBaseContext(), home.class);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}