package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class viewAssessment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment);


        // update error word

        FileInputStream fis2 = null;
        TextView tx2 = (TextView) findViewById(R.id.para_wrong_view1);

        try {
            fis2 = openFileInput("Para_words_wrong_txt");
            InputStreamReader isr = new InputStreamReader(fis2);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String transcript_txt;

            while((transcript_txt = br.readLine())!=null){
                sb.append(transcript_txt).append("\n");
            }


            tx2.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tx2.setText("None");
        } catch (IOException e) {
            e.printStackTrace();
        }



        // question 1
        FileInputStream fis3 = null;
        TextView tx3 = (TextView) findViewById(R.id.question1_view1);

        try {
            fis3 = openFileInput("question1_txt");
            InputStreamReader isr = new InputStreamReader(fis3);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String transcript_txt;

            while((transcript_txt = br.readLine())!=null){
                sb.append(transcript_txt).append("\n");
            }


            tx3.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tx3.setText("None");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // question 2
        FileInputStream fis4 = null;
        TextView tx4 = (TextView) findViewById(R.id.question2_view1);

        try {
            fis4 = openFileInput("question2_txt");
            InputStreamReader isr = new InputStreamReader(fis4);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String transcript_txt;

            while((transcript_txt = br.readLine())!=null){
                sb.append(transcript_txt).append("\n");
            }


            tx4.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tx4.setText("None");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // words wrong
        FileInputStream fis6 = null;
        TextView tx6 = (TextView) findViewById(R.id.words_wrong_view1);

        try {
            fis6 = openFileInput("words_wrong_txt");
            InputStreamReader isr = new InputStreamReader(fis6);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String transcript_txt;

            while((transcript_txt = br.readLine())!=null){
                sb.append(transcript_txt).append("\n");
            }


            tx6.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tx6.setText("None");
        } catch (IOException e) {
            e.printStackTrace();
        }




        // letters wrong
        FileInputStream fis8 = null;
        TextView tx8 = (TextView) findViewById(R.id.letters_wrong_view1);

        try {
            fis8 = openFileInput("letters_wrong_txt");
            InputStreamReader isr = new InputStreamReader(fis8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String transcript_txt;

            while((transcript_txt = br.readLine())!=null){
                sb.append(transcript_txt).append("\n");
            }


            tx8.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tx8.setText("None");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // literacy level
        FileInputStream fis9 = null;
        TextView tx9 = (TextView) findViewById(R.id.literacy_level_view);

        try {
            fis9 = openFileInput("literacy_level_txt");
            InputStreamReader isr = new InputStreamReader(fis9);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String transcript_txt;

            while((transcript_txt = br.readLine())!=null){
                sb.append(transcript_txt).append("\n");
            }


            tx9.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tx9.setText("None");
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public void exportData(View v){
        Toast.makeText(getApplicationContext(), "Data has been exported successfully", Toast.LENGTH_LONG).show();
    }

    public void newAssessment(View v){
        Intent myIntent = new Intent(getBaseContext(), Begin_Assessment.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
