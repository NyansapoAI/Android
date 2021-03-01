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
import androidx.appcompat.app.AppCompatActivity;
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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class word_assessment extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    String words;
    String[] word;
    int error_count;
    int word_count;
    int words_tried;
    SpeechAsync speechAsync;


    // assessment content
    Assessment_Content assessment_content;
    Assessment assessment;
    String ASSESSMENT_KEY;


    // words wrong
    String words_wrong ="";
    String words_correct = "";
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
        setContentView(R.layout.activity_word_assessment);

        Toast.makeText(this, "Click on the Record Button to read or click on change to change the prompt", Toast.LENGTH_LONG).show();

        // will replace later

        Intent intent = getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment");
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY();

        assessment_content = new Assessment_Content();
        word = getWords(ASSESSMENT_KEY);

        //words = "Table Pen Child Fish Lion Man Tree Door";
        //word = words.split(" ");

        //ui components
        record_button = findViewById(R.id.record_button);
        change_button = findViewById(R.id.change_button);
        assessment_card = findViewById(R.id.assessment_card);

        // progressbar
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setMax(15000);
        progressBar.setProgress(0);

        // intialize
        error_count = 0;
        word_count = 0;
        words_tried = 0;

        // assign first word
        assessment_card.setText(word[0].trim());

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
                //changeWord1();
                //Toast.makeText(word_assessment.this, "Read ")
            }
        });
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStudent(v);
            }
        });

    }

    Drawable drawable;

    public void recordStudent(View v){

        if(!transcriptStarted){
            drawable = assessment_card.getBackground();
            Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
            //read_button.setBackgroundColor(Color.BLUE);
            int lightblue = Color.parseColor("#82b6ff"); //light blue
            //int lightblue = Color.parseColor("#8B4513");

            int lightbrown = Color.parseColor("#eecab1"); // light brown
            //int lightbrown = Color.parseColor("#7ab121"); // Green
            //int lightbrown = Color.parseColor("#FFFF00"); // bright yellow


            newDrawable.setColorFilter(new PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY));
            assessment_card.setBackground(newDrawable);
            assessment_card.setTextColor(lightbrown);

            SpeechAsync speechAsync = new SpeechAsync();
            speechAsync.execute(v);
            transcriptStarted = true;
        }



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


    public void changeWord(){
        progressBar.setProgress(0);
        if(words_tried > 4){ // if 6 has been tried
            if(error_count < 2){
                goToThankYou();
            }else{
                goToLetter();
            }
        }

        else if(word_count < word.length -1){
            word_count +=1; // increment sentence count
            words_tried +=1;
            assessment_card.setText(word[word_count].trim());
        }else{
            word_count = 0; // dont know why
        }
    }

    public void changeWord1(){

        if(word_count < word.length -1){
            word_count +=1; // increment sentence count
            assessment_card.setText(word[word_count].trim());
        }else{
            word_count = 0; // dont know why
        }
    }

    public void startLetter(View v){
        //mediaPlayer.release();
        Intent myIntent = new Intent(getBaseContext(), letter_assessment.class);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

            if(s.equalsIgnoreCase("canceled")){
                Toast.makeText(word_assessment.this,"Internet Connection Failed", Toast.LENGTH_LONG).show();
            }else if (s.equalsIgnoreCase("no match")){
                Toast.makeText(word_assessment.this,"Try Again", Toast.LENGTH_LONG).show();
            }else {
                String error_txt = SpeechRecognition.compareTranscript(expected_txt, s);

                if (SpeechRecognition.countError(error_txt) == 0) {
                    words_correct += expected_txt.trim()+",";
                }else{
                    words_wrong += error_txt.trim()+",";
                }
                error_count +=  SpeechRecognition.countError(error_txt);
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), SpeechRecognition.removeDuplicates(s) , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();
                /*if (error_count > 1) { // if error less than 3 move to story level
                    //goToLetter();
                } else if (word_count > 3 && error_count < 2) { // got to thank you page if error is less than 2
                    //goToThankYou();
                } else */
                if (s != "no match") changeWord();
            }
            reco.close();
        }
    }

    void goToLetter(){
        Intent myIntent = new Intent(getBaseContext(), letter_assessment.class);
        assessment.setWORDS_WRONG(words_wrong);
        assessment.setWORDS_CORRECT(words_correct);
        myIntent.putExtra("Assessment",assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    void goToThankYou(){
        Intent myIntent = new Intent(getBaseContext(), thankYou.class);
        assessment.setWORDS_WRONG(words_wrong);
        assessment.setWORDS_CORRECT(words_correct);
        assessment.setLEARNING_LEVEL("WORD");
        myIntent.putExtra("Assessment",assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public String[] getWords(String key){
        switch (key){
            case "3":{
                return assessment_content.getW3();
            }
            case "4":{
                return assessment_content.getW4();
            }
            case "5":{
                return assessment_content.getW5();
            }
            case "6":{
                return assessment_content.getW6();
            }
            case "7":{
                return assessment_content.getW7();
            }
            case "8":{
                return assessment_content.getW8();
            } case "9":{
                return assessment_content.getW9();
            }
            case "10":{
                return assessment_content.getW10();
            } default: return assessment_content.getW3();
        }
    }
}
