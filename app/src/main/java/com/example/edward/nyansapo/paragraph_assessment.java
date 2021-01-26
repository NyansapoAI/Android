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
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.PropertyCollection;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class paragraph_assessment extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Button paragraphButton, changeButton, record_button;
    String paragraph;
    String[] sentences;
    int error_count;
    int sentence_count;
    int tries;

    // assessment content
    Assessment_Content assessment_content;
    Assessment assessment;
    String ASSESSMENT_KEY;

    // paragraph
    String paragraph_words_wrong = "";


    // instructor
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
        setContentView(R.layout.activity_paragraph_assessment);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Toast.makeText(this, "Click on the Record Button to read or click on change to change the prompt", Toast.LENGTH_LONG).show();

        assessment = intent.getParcelableExtra("Assessment");
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY();

        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();

        assessment_content = new Assessment_Content();
        String[] para = getPara(ASSESSMENT_KEY);

        paragraph = para[0];
        //Toast.makeText(this, intent.getStringExtra("paragraph"), Toast.LENGTH_LONG);
        if (bundle.getString("paragraph").equalsIgnoreCase("0")){
            paragraph = para[0];
        }else{
            paragraph = para[1];
        }
        sentences = paragraph.split("[.]");
        paragraphButton = findViewById(R.id.paragraph1);


        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.paragraph);
        //mediaPlayer.start();

        //changeButton.setEnabled(false);

        // progressbar
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setMax(15000);
        progressBar.setProgress(0);

        error_count = 0;
        sentence_count = 0;
        tries = 0;

        paragraphButton.setText(sentences[0].trim());
        changeButton = findViewById(R.id.change_button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeSentence();
                Toast.makeText(paragraph_assessment.this, "Can't Change Sentence", Toast.LENGTH_SHORT).show();
            }
        });

        record_button = findViewById(R.id.record_button);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStudent(v);
            }
        });

        paragraphButton.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View view) {
                recordStudent(view);
            }
        });
    }
    Drawable drawable;

    public void recordStudent(View v){
        //mediaPlayer.release();


        if(!transcriptStarted){
            drawable = paragraphButton.getBackground();
            Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
            //read_button.setBackgroundColor(Color.BLUE);
            //int lightblue = Color.parseColor("#82b6ff"); light blue
            int lightblue = Color.parseColor("#8B4513");

            //int lightbrown = Color.parseColor("#eecab1"); // light brown
            //int lightbrown = Color.parseColor("#7ab121"); // Green
            int lightbrown = Color.parseColor("#FFFF00"); // bright yellow


            newDrawable.setColorFilter(new PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY));
            paragraphButton.setBackground(newDrawable);
            paragraphButton.setTextColor(lightbrown);


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

    public void changeSentence(){
        progressBar.setProgress(0);
        //Toast.makeText(this, Integer.toString(error_count) , Toast.LENGTH_LONG).show();
        tries= 0; // everytime a sentence is changed tries go to one
        if(sentence_count < sentences.length -1){
            sentence_count +=1; // increment sentence count
            paragraphButton.setText(sentences[sentence_count].trim());
        }else{ // move a level
            if(error_count > 3){
                goToWord();
            }else{
                goToStory();
            }
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
            paragraphButton = findViewById(R.id.paragraph1);
            expected_txt = paragraphButton.getText().toString();
            //paragraphButton.setEnabled(false);
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

                    PropertyCollection properties = result.getProperties();
                    String property = properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult);
                    
                    //Toast.makeText(paragraph_assessment.this, property, Toast.LENGTH_LONG).show();

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

            paragraphButton.setBackground(drawable);
            paragraphButton.setTextColor(Color.BLACK);

            if(mediaStarted){
                mediaRecorder.stop();
                mediaRecorder.release();
                progressBar.setProgress(0);
                mediaStarted = false;
            }
            transcriptStarted = false;

            if (s.equalsIgnoreCase("canceled")) {
                Toast.makeText(paragraph_assessment.this, "Internet Connection Failed", Toast.LENGTH_LONG).show();
            } else if (s.equalsIgnoreCase("no match")) {
                Toast.makeText(paragraph_assessment.this, "Try Again", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //s = SpeechRecognition.removeDuplicates(s);
                String error_txt = SpeechRecognition.compareTranscript(expected_txt, s);
                /*if (SpeechRecognition.countError(error_txt) != 0) { // if no erro
                    //words_correct += ","+expected_txt.trim();
                    paragraph_words_wrong +=  error_txt.trim()+",";
                }*/

                //error_count += SpeechRecognition.countError(error_txt);
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), SpeechRecognition.removeDuplicates(s), Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();
                /*if (error_count > 4) { // if error less than 3 move to story level
                    //goToWord();
                } */
                if( SpeechRecognition.countError(error_txt) >2 || s.split(" ").length < 2){
                    if( tries < 1 ){
                        tries ++; // incremnent tries
                        Toast.makeText(view.getContext(), "Try Again!" , Toast.LENGTH_LONG).show();
                    }else{
                        error_count += SpeechRecognition.countError(error_txt);
                        if (SpeechRecognition.countError(error_txt) != 0) { // if no erro
                            //words_correct += ","+expected_txt.trim();
                            paragraph_words_wrong +=  error_txt.trim()+",";
                        }

                        //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                        /*Toast.makeText(view.getContext(), SpeechRecognition.removeDuplicates(s), Toast.LENGTH_LONG).show();
                        Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                        Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                        Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();*/
                        changeSentence();
                    }

                }else{
                    error_count += SpeechRecognition.countError(error_txt);
                    if (SpeechRecognition.countError(error_txt) != 0) { // if no erro
                        //words_correct += ","+expected_txt.trim();
                        paragraph_words_wrong +=  error_txt.trim()+",";
                    }

                    //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                    /*Toast.makeText(view.getContext(), SpeechRecognition.removeDuplicates(s), Toast.LENGTH_LONG).show();
                    Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                    Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                    Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();*/
                    changeSentence();
                }


            }
            reco.close();
        }
    }

    public void goToStory(){
        assessment.setPARAGRAPH_WORDS_WRONG(paragraph_words_wrong); // set words wrong
        Intent myIntent = new Intent(getBaseContext(), story_assessment.class);
        myIntent.putExtra("Assessment",assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goToWord(){
        assessment.setPARAGRAPH_WORDS_WRONG(paragraph_words_wrong);
        Intent myIntent = new Intent(getBaseContext(), word_assessment.class);
        myIntent.putExtra("Assessment",assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public String[] getPara(String key){
        switch (key){
            case "3":{
                return assessment_content.getP3();
            }
            case "4":{
                return assessment_content.getP4();
            }
            case "5":{
                return assessment_content.getP5();
            }
            case "6":{
                return assessment_content.getP6();
            }
            case "7":{
                return assessment_content.getP7();
            }
            case "8":{
                return assessment_content.getP8();
            } case "9":{
                return assessment_content.getP9();
            }
            case "10":{
                return assessment_content.getP10();
            } default: return assessment_content.getP3();
        }
    }
}
