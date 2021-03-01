package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class settings extends AppCompatActivity {

    String instructor_id;

    Button  update_button,  logout_button;
    TextView firstname, lastname, email,password;

    dataBaseHandler databasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), home.class));
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(settings.this).toBundle());
            }
        });

        // database
        databasehelper = new dataBaseHandler(settings.this);


        // get intent values
        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");

        // ui code
        update_button = findViewById(R.id.update_button);

        logout_button = findViewById(R.id.logout_button);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateInfo();
                Toast.makeText(settings.this, "Under Development", Toast.LENGTH_LONG).show();
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databasehelper.deleteInstructor(instructor_id);
                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(settings.this).toBundle());

            }
        });


        setInstructorInfo();

    }

    public void setInstructorInfo(){

        //Get instructor
        Instructor instructor;
        instructor =  databasehelper.getInstructorByID(instructor_id);
        //Instructor ins = databasehelper.getInstructorByEmail("edward@kijenzi.com");

        //Toast.makeText(this, instructor.getCloud_id(), Toast.LENGTH_LONG).show();

        //Toast.makeText(this, ins.getLocal_id(), Toast.LENGTH_LONG).show();

        // set Text to screen
        email.setText(instructor.getEmail());
        firstname.setText(instructor.getFirstname());
        lastname.setText(instructor.getLastname());

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


}