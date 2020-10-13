package com.example.edward.nyansapo;

import android.net.rtp.AudioStream;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionCanceledEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.util.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.EventListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.*;
import java.util.stream.*;



public class SpeechRecognition {

    public static String audioFileName;

    public static String convertSpeech(View v) {
/*

        //TextView txt = (TextView) this.findViewById(R.id.hello); // 'hello' is the ID of your text view

        String return_val = "";

        try {

            // Replace below with your own subscription key
            String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

            // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
            String serviceRegion = "eastus";

            String endpoint = "275310be-2c21-4131-9609-22733b4e0c04";

            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setEndpointId(endpoint);
            assert(config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);

            Future<SpeechRecognitionResult> task= null;
            //ssert(task != null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
                    assert(task != null);
                }
            }).start();

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            assert(task != null);
            SpeechRecognitionResult result = task.get();
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                // txt.setText(result.toString());
                //Toast.makeText(this,result.toString(),Toast.LENGTH_LONG);
                return result.getText();
            }
            else {
                reco.close();
                return "Didn't get result";
            }
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            return "";
        }

*/


        //TextView txt = (TextView) this.findViewById(R.id.hello); // 'hello' is the ID of your text view


        String return_val = "";

        try {

            // Replace below with your own subscription key
            String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

            // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
            String serviceRegion = "eastus";

            String endpoint = "275310be-2c21-4131-9609-22733b4e0c04";

            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setEndpointId(endpoint);
            assert(config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            SpeechRecognitionResult result = task.get();
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
               // txt.setText(result.toString());
                //Toast.makeText(this,result.toString(),Toast.LENGTH_LONG);
                return result.getText();
            }
            else {
                reco.close();
                return "Didn't get result";
            }
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            return "";
        }

    }


    public static String convertSpeech1(String filename){
        int err_key =0;
        try {
            // Replace below with your own subscription key
            String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

            // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
            String serviceRegion = "eastus";

            // Replace below with your own filename.
            //String audioFileName = filename;
            /*
            audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/nyansapo_audio_record.wav";
             */
            audioFileName = filename;

            //Toast.makeText(getApplicationContext(), filename , Toast.LENGTH_SHORT).show();
            err_key = 1;
            int exitCode = 1;
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            assert(config != null);

            err_key = 2;


            ByteArrayOutputStream b_out = new ByteArrayOutputStream();


            AudioConfig audioInput = AudioConfig.fromWavFileInput("res/raw/speech_226_2.wav");
            //AudioConfig audioInput = AudioConfig.
            assert(audioInput != null);

            err_key = 3;
            SpeechRecognizer reco = new SpeechRecognizer(config, audioInput);
            assert(reco != null);

            err_key = 4;
            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            err_key = 5;
            SpeechRecognitionResult result = task.get();
            assert(result != null);

            if(result.getReason() == ResultReason.RecognizedSpeech){
                return result.getText();
            }else if(result.getReason() == ResultReason.NoMatch){
                return "no match";
            }else if(result.getReason() == ResultReason.Canceled){
                return "canceled";
            }

            reco.close();

            System.exit(exitCode);
        } catch (Exception ex) {
            return "error" + err_key;
        }

        return "nothing"; // take out later

    }

    private static Semaphore stopTranslationWithFileSemaphore;
    static SpeechRecognizer  recognizer;

    public static void convertContinuous(View v){
        String TAG = "AZURE";

        final String[] return_val = {""};


        try {

            // Replace below with your own subscription key
            String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

            // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
            String serviceRegion = "eastus";

            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            assert(config != null);


            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);

            //Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            reco.stopContinuousRecognitionAsync();

            reco.recognizing.addEventListener(new EventHandler<SpeechRecognitionEventArgs>() {
                @Override
                public void onEvent(Object o, SpeechRecognitionEventArgs speechRecognitionEventArgs) {

                }
            });



        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            //return "";
        }


    }


    public static String removeDuplicates(String txt){

        String str = "";
        txt = txt.replaceAll("[.]", ""); // remove all .
        txt = txt.replaceAll("[,]",""); // remove all
        txt = txt.toLowerCase(); // lower case

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            str = Arrays.stream( txt.split("\\s+")).distinct().collect(Collectors.joining(" ") );
        }

        return str;
    }




    public static String compareTranscript(String expected_txt, String transcript_txt){
        String error_txt;
        error_txt = "";

       transcript_txt = removeDuplicates(transcript_txt);


        // convert string into a string array of the words
        String[] expected_words = expected_txt.split(" "); // I live in Kenya
        String[] transcript_words = transcript_txt.split(" "); // I live in Kenya

        // clean up array and remove . and ,
        for(int i=0;i<expected_words.length;i++){
            if(expected_words[i].contains("."))
                expected_words[i] = expected_words[i].substring(0,(expected_words[i].length()-1));
            if(expected_words[i].contains(",")) expected_words[i] = expected_words[i].substring(0,(expected_words[i].length()-1));
        }

        for(int i=0;i<transcript_words.length;i++){
            if(transcript_words[i].contains("."))
                transcript_words[i] = transcript_words[i].substring(0,(transcript_words[i].length()-1));
            if(transcript_words[i].contains(",")) transcript_words[i] = transcript_words[i].substring(0,(transcript_words[i].length()-1));
        }

        // find errors
        if(transcript_words.length < expected_words.length) {
            for (int i = 0; i < transcript_words.length; i++) {
                if (transcript_words[i].compareToIgnoreCase(expected_words[i]) == 0) continue;
                else {
                    error_txt += expected_words[i] + ",";
                    //if(!transcript_txt.contains(expected_words[i])){ // if mismatched expected work is in transcript
                     //   error_txt += expected_words[i] + ",";
                    //}

                }
            }

            for(int i = transcript_words.length; i < expected_words.length; i++){ // add other words not
                error_txt +=  expected_words[i] +",";
                /*if(!transcript_txt.contains(expected_words[i])){ // if mismatched expected work is in transcript
                    error_txt += expected_words[i] + ",";
                }*/
            }

        }else{
            for (int i = 0; i < expected_words.length; i++) {
                if (transcript_words[i].compareToIgnoreCase(expected_words[i]) == 0) continue;
                else {
                    error_txt += expected_words[i] + ",";
                    //if(!transcript_txt.contains(expected_words[i])){ // if mismatched expected work is in transcript
                    //    error_txt += expected_words[i] + ",";
                    //}
                }
            }
        }

        //String er_txt="";
        if(error_txt.contains(",")){
            return error_txt.substring(0, error_txt.length()-1);
        }


        return error_txt;



        /*String error_txt;
        error_txt = "";

        transcript_txt = removeDuplicates(transcript_txt);

        // convert string into a string array of the words
        String[] expected_words = expected_txt.split(" ");
        String[] transcript_words = transcript_txt.split(" ");

        // clean up array and remove . and ,
        for(int i=0;i<expected_words.length;i++){
            if(expected_words[i].contains("."))
                expected_words[i] = expected_words[i].substring(0,(expected_words[i].length()-1));
            if(expected_words[i].contains(",")) expected_words[i] = expected_words[i].substring(0,(expected_words[i].length()-1));
        }

        for(int i=0;i<transcript_words.length;i++){
            if(transcript_words[i].contains("."))
                transcript_words[i] = transcript_words[i].substring(0,(transcript_words[i].length()-1));
            if(transcript_words[i].contains(",")) transcript_words[i] = transcript_words[i].substring(0,(transcript_words[i].length()-1));
        }

        // find errors
        if(transcript_words.length < expected_words.length) {
            for (int i = 0; i < transcript_words.length; i++) {
                if (transcript_words[i].compareToIgnoreCase(expected_words[i]) == 0) continue;
                else {
                    error_txt += expected_words[i] + " ";
                }
            }
        }else{
            for (int i = 0; i < expected_words.length; i++) {
                if (transcript_words[i].compareToIgnoreCase(expected_words[i]) == 0) continue;
                else {
                    error_txt += expected_words[i] + " ";
                }
            }
        }

        return error_txt;*/


    }



    public static int countError(String error_txt){
        if(error_txt=="") return 0;
        String[] error_wrd = error_txt.split(",");
        return error_wrd.length;
    }




}




