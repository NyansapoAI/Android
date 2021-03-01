package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class index extends AppCompatActivity {

    // declare database connection
    dataBaseHandler databasehelper;

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        // Initialize DatabaseHelper
        databasehelper = new dataBaseHandler(index.this); // might need to try catch

        logo = findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });
        //checkUser();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                checkUser();
            }
        }, 1000);

    }


    private void checkUser() {

        String  instructor_id = "0";
        Instructor instructor  =databasehelper.getInstructor();


        //Toast.makeText(index.this, instructor_id, Toast.LENGTH_SHORT).show();

        if (instructor != null)
        {
            //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            instructor_id = instructor.getCloud_id();
            Intent myIntent = new Intent(getBaseContext(), home.class);
            myIntent.putExtra("instructor_id", instructor_id); // its id for now
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(index.this).toBundle());

        }else{
            //Toast.makeText(this, "Sign", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            //myIntent.putExtra("instructor_id", instructor_id); // its id for now
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(index.this).toBundle());
        }

    }
}