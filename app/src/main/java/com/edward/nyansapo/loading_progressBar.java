package com.nyansapo;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;

public class loading_progressBar {

    Activity activity;
    AlertDialog alertDialog;

    loading_progressBar(Activity activity){
        this.activity = activity;
    }

    void showDialog(){
        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        alertbuilder.setView(layoutInflater.inflate(R.layout.progress_layout, null));
        alertDialog = alertbuilder.create();
        alertDialog.show();
    }

    void dismissDialog(){
        alertDialog.dismiss();
    }
}
