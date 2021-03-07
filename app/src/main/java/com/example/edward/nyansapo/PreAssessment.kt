package com.example.edward.nyansapo;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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


public class PreAssessment extends AppCompatActivity implements  View.OnClickListener {

    // button ui
    Button next_button;
    Button record_button;
    Button read_button;



    // Audio Recording Settings
    String outputFile;
    MediaRecorder myAudioRecorder;

    //
    Integer button_toggle;

    //
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    // Permission
    final int  REQUEST_PERSMISSION_CODE = 1000;
    String filename = "/dev/null";


    // Asyn stuff
    SpeechAsync speechAsync;

    // Assessment key
    String ASSESSMENT_KEY = "3";
    String student_id="";

    // img
    ImageView arrow_img;
    Animation arrow_animation_leftToRight;
    Animation arrow_animation_blink;
    Animation arrow_animation_fadeOut;

    String instructor_id;


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
        setContentView(R.layout.activity_pre_assessment);

        Bundle bundle = getIntent().getExtras();
        ASSESSMENT_KEY = bundle.getString("ASSESSMENT_KEY");
        student_id = bundle.getString("student_id");

        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();

        //Toast.makeText(getApplicationContext(), "Click on the record button and read!", Toast.LENGTH_LONG).show();

        // Request Runtime Permission
        if(!checkPermissionFromDevice())
            requestPermission();

        // assign buttons to xml components
        next_button = findViewById(R.id.next_button);
        record_button = findViewById(R.id.record_button);
        read_button = findViewById(R.id.read_button);


        // progressbar
        progressBar = findViewById(R.id.progressBar2);

        // set onclick listeners
        next_button.setOnClickListener(this);
        record_button.setOnClickListener(this);
        read_button.setOnClickListener(this);

        // button toggle
        button_toggle = 1; // 1 will record if button is clicked and -1 will stop if button is clicked

        // Animation stuff
        arrow_img = findViewById(R.id.arrow_img);


        arrow_animation_leftToRight = AnimationUtils.loadAnimation(this, R.anim.lefttoright);

        arrow_animation_leftToRight.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                arrowBlink();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        arrow_img.startAnimation(arrow_animation_leftToRight);

        arrow_animation_blink = AnimationUtils.loadAnimation(this, R.anim.blink_anim);

        arrow_animation_blink.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                arrowFadeOut();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        arrow_animation_fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);


        //progressBar

        progressBar.setMax(15000);
        progressBar.setProgress(0);




    }


    public void arrowBlink(){
        arrow_img.startAnimation(arrow_animation_blink);
    }

    public void arrowFadeOut(){
        arrow_img.startAnimation(arrow_animation_fadeOut);
    }

    public void goHome(View v){
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    public void recordStudent(View v){

       Intent myIntent = new Intent(getBaseContext(), paragraph.class);
        Assessment assessment = new Assessment(); // create new assessment object
        assessment.setASSESSMENT_KEY(ASSESSMENT_KEY); // assign proper key
        assessment.setSTUDENT_ID(student_id); // needs to change to cloud id
        myIntent.putExtra("Assessment", assessment); //sent next activity
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());



    }

    String txt;

    Drawable drawable;

    public void Func(View v){

        startRecording();
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // code to execute repeatedly
                double num = getAmplitudeEMA();
                progressBar.setProgress((int) num);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        if(!transcriptStarted){
            drawable = read_button.getBackground();
            Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
            //read_button.setBackgroundColor(Color.BLUE);
            int lightblue = Color.parseColor("#82b6ff"); //light blue
            //int lightblue = Color.parseColor("#8B4513");

            //int lightbrown = Color.parseColor("#eecab1"); // light brown
            //int lightbrown = Color.parseColor("#7ab121"); // Green
            int lightbrown = Color.parseColor("#FFFF00"); // bright yellow


            newDrawable.setColorFilter(new PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY));
            read_button.setBackground(newDrawable);
            read_button.setTextColor(lightbrown);

            SpeechAsync speechAsync = new SpeechAsync();
            speechAsync.execute(v);
            transcriptStarted = true;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.read_button:
                //arrow_img.setVisibility(View.INVISIBLE);




                Func(v);



                //startRecording();
                //RecordAsync recordAsync = new RecordAsync();
                //recordAsync.execute(v);



                break;
            case R.id.record_button:
                Func(v);
                /*
                SpeechAsync speechAsync1 = new SpeechAsync();
                speechAsync1.execute(v);
                startRecording();
                ScheduledThreadPoolExecutor exec1 = new ScheduledThreadPoolExecutor(1);
                exec1.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        // code to execute repeatedly
                        double num = getAmplitudeEMA();
                        progressBar.setProgress((int) num);
                    }
                }, 0, 100, TimeUnit.MILLISECONDS);
                */
                break;
            case R.id.next_button: // go to the assessment
            {
                //Toast.makeText(this, instructor_id +" pressed", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getBaseContext(), paragraph.class);
                Assessment assessment = new Assessment(); // create new assessment object
                assessment.setASSESSMENT_KEY(ASSESSMENT_KEY); // assign proper key
                assessment.setSTUDENT_ID(student_id);
                myIntent.putExtra("Assessment", assessment); //sent next activity
                myIntent.putExtra("instructor_id", instructor_id);
                //Toast.makeText(this, assessment.toString() +"  "+ assessment.getSTUDENT_ID(), Toast.LENGTH_SHORT).show();
                startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


                //Toast.makeText(PreAssessment.this, Double.toString(num), Toast.LENGTH_SHORT).show();

            }
                break;
            default:
                throw new IllegalStateException("Unexpected value");
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
        }, REQUEST_PERSMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERSMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {

        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        int internet_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED && internet_permission == PackageManager.PERMISSION_GRANTED;

    }

    private class SpeechAsync extends AsyncTask<View,String,String >{

        // Replace below with your own subscription key
        String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

        // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
        String serviceRegion = "eastus";

        String endpoint = "275310be-2c21-4131-9609-22733b4e0c04";

        SpeechConfig config;

        SpeechRecognizer reco;

        View view;

        @Override
        protected void onPreExecute() {
            //read_button.setEnabled(false);
            //record_button.setEnabled(false);
            super.onPreExecute();
            config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setEndpointId(endpoint);
        }

        @Override
        protected String doInBackground(View... views) {
            try{
                view = views[0];

                reco = new SpeechRecognizer(config);

                SpeechRecognitionResult result = null;

                Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
                assert(task != null);


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
            read_button.setBackground(drawable);
            read_button.setTextColor(Color.BLACK);
            if(mediaStarted){
                mediaRecorder.stop();
                mediaRecorder.release();
                progressBar.setProgress(0);
                mediaStarted = false;
            }
            transcriptStarted = false;

            //read_button.setEnabled(true);
            //record_button.setEnabled(true);
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("canceled")){
                Toast.makeText(PreAssessment.this,"Internet Connection Failed", Toast.LENGTH_LONG).show();
            }else if (s.equalsIgnoreCase("no match")){
                Toast.makeText(PreAssessment.this,"Try Again", Toast.LENGTH_LONG).show();
            }else{
                String err_txt = SpeechRecognition.compareTranscript("I Live in Kenya",s);
                int count = SpeechRecognition.countError(err_txt);
                if(count < 2){
                    recordStudent(view);
                }
            }

            reco.close();
        }

    }
}
