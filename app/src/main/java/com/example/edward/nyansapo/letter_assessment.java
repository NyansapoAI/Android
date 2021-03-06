package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class letter_assessment extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    String letters;
    String[] letter;

    // intialize
    int error_count = 0;
    int letter_count = 0;
    int letters_tried = 0;

    SpeechAsync speechAsync;


    // assessment content
    Assessment_Content assessment_content;
    Assessment assessment;
    String ASSESSMENT_KEY;


    // letters wrong
    String letters_wrong ="";
    String letters_correct = "";


    // UI
    Button record_button, assessment_card, change_button;


    String instructor_id;


    // media
    MediaRecorder mediaRecorder;
    String filename = "/dev/null";

    //audio stuff

    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    // progress bar

    ProgressBar progressBar;

    /// Control variables or code locks
    boolean mediaStarted = false;
    boolean transcriptStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_assessment);

        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.letter);
        //mediaPlayer.start();

        // will replace later

        Toast.makeText(this, "Click on the Record Button to read or click on change to change the prompt", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment");
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY();

        assessment_content = new Assessment_Content();
        letter = getLetters(ASSESSMENT_KEY);

        //letters = "a d f g k i l m o v q r s t b j";
        //letter = letters.split(" ");

        //ui components
        record_button = findViewById(R.id.record_button);
        change_button = findViewById(R.id.change_button);
        assessment_card = findViewById(R.id.assessment_card);

        error_count = 0;
        letter_count = 0;
        letters_tried = 0;

        // progressbar
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setMax(15000);
        progressBar.setProgress(0);

        // assign first word
        assessment_card.setText(letter[0].trim());

        // on click listeners
        assessment_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStudent(v);
            }
        });
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeLetter();
            }
        });
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStudent(v);
            }
        });

    }

    public void changeLetter(){
        if(letters_tried > 4){
            goToThankYou();
        }

        else if(letter_count < letter.length -1){
            letter_count +=1; // increment sentence count
            letters_tried +=1;
            assessment_card.setText(letter[letter_count].trim());
        }else{
            letter_count = 0; // dont know why
        }
    }


    public void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        //this.mediaRecorder.setOutputFile(this.file.getAbsolutePath());
        mediaRecorder.setOutputFile(filename);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("Prepare", "prepare() failed");
        }

        try{
            mediaRecorder.start();
            mediaStarted = true;
        } catch (Exception ex) {
            //Toast.makeText(PreAssessment.this, "No feedback", Toast.LENGTH_LONG).show();
            mediaStarted = false;
        }
        //Toast.makeText(PreAssessment.this, "Started Recording", Toast.LENGTH_SHORT).show();
    }

    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public double getAmplitude() {
        if (mediaRecorder != null)
            return  (mediaRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public void changeLetter1(){
        if(letter_count < letter.length -1){
            letter_count +=1; // increment sentence count
            assessment_card.setText(letter[letter_count].trim());
        }else{
            letter_count = 0; // dont know why
        }
    }

    Drawable drawable;

    public void recordStudent(View v){
       // mediaPlayer.release();
        if(!transcriptStarted){
            drawable = assessment_card.getBackground();
            Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
            //read_button.setBackgroundColor(Color.BLUE);
            //int lightblue = Color.parseColor("#82b6ff"); light blue
            int lightblue = Color.parseColor("#8B4513");

            //int lightbrown = Color.parseColor("#eecab1"); // light brown
            int lightbrown = Color.parseColor("#7ab121"); // Green
            //int lightbrown = Color.parseColor("#FFFF00"); // bright yellow


            newDrawable.setColorFilter(new PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY));
            assessment_card.setBackground(newDrawable);
            assessment_card.setTextColor(lightbrown);

            SpeechAsync speechAsync = new SpeechAsync();
            speechAsync.execute(v);
            transcriptStarted = true;
        }
        //SpeechAsync speechAsync = new SpeechAsync();
        //speechAsync.execute(v);


        startRecording();
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // code to execute repeatedly
                double num = getAmplitudeEMA();
                progressBar.setProgress((int) num);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private class SpeechAsync extends AsyncTask<View,String,String > {

        // Replace below with your own subscription key
        String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

        // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
        String serviceRegion = "eastus";

        String endpoint = "275310be-2c21-4131-9609-22733b4e0c04";

        SpeechConfig config;

        SpeechRecognizer reco;

        View view;

        String expected_txt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setEndpointId(endpoint);
            assessment_card = findViewById(R.id.assessment_card);
            expected_txt = assessment_card.getText().toString();
            //assessment_card.setEnabled(false);
            //record_button.setEnabled(false);
        }

        @Override
        protected String doInBackground(View... views) {
            try{
                view = views[0];

                reco = new SpeechRecognizer(config);

                SpeechRecognitionResult result = null;

                Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
                assert(task != null);

                // Note: this will block the UI thread, so eventually, you want to
                //        register for the event (see full samples)
                try {
                    result = task.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                assert(result != null);

                if(result.getReason() == ResultReason.RecognizedSpeech){
                    return result.getText();
                }else if(result.getReason() == ResultReason.NoMatch){
                    return "no match";
                }else if(result.getReason() == ResultReason.Canceled){
                    return "canceled";
                }

            }catch (Error err){
                return " Error"+ err.getMessage().toString();
            }
            return null;
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            reco.close();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            assessment_card.setBackground(drawable);
            assessment_card.setTextColor(Color.BLACK);

            if(mediaStarted){
                mediaRecorder.stop();
                mediaRecorder.release();
                progressBar.setProgress(0);
                mediaStarted = false;
            }
            transcriptStarted = false;

            //progressBar.setBackgroundColor(2);
            if(s.equalsIgnoreCase("canceled")){
                Toast.makeText(letter_assessment.this,"Internet Connection Failed", Toast.LENGTH_LONG).show();
            }else if (s.equalsIgnoreCase("no match")){
                Toast.makeText(letter_assessment.this,"Try Again", Toast.LENGTH_LONG).show();
            }else {
                String error_txt = SpeechRecognition.compareTranscript(expected_txt, s);

                if (SpeechRecognition.countError(error_txt) == 0) {
                    letters_correct += expected_txt.trim()+",";
                }else{
                    letters_wrong += error_txt.trim()+",";
                }
                error_count += SpeechRecognition.countError(error_txt);
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();
                /*if (error_count > 2) { // if error less than 3 move to story level
                    //goToThankYou();
                } else if (letter_count > 3 && error_count < 2) { // got to thank you page if error is less than 2
                    //goToThankYou();
                } else */
                if (s != "no match") changeLetter();
            }
            reco.close();
        }

    }

    private void goToThankYou() {
        Intent myIntent = new Intent(getBaseContext(), thankYou.class);
        assessment.setLETTERS_CORRECT(letters_correct);
        assessment.setLETTERS_WRONG(letters_wrong);
        assessment.setLEARNING_LEVEL("LETTER");
        myIntent.putExtra("Assessment",assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public String[] getLetters(String key){
        switch (key){
            case "3":{
                return assessment_content.getL3();
            }
            case "4":{
                return assessment_content.getL4();
            }
            case "5":{
                return assessment_content.getL5();
            }
            case "6":{
                return assessment_content.getL6();
            }
            case "7":{
                return assessment_content.getL7();
            }
            case "8":{
                return assessment_content.getL8();
            } case "9":{
                return assessment_content.getL9();
            }
            case "10":{
                return assessment_content.getL10();
            } default: return assessment_content.getL3();
        }
    }
}
