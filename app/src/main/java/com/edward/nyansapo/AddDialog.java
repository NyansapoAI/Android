package com.nyansapo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddDialog extends AppCompatDialogFragment {
    private AddDialogListener Listener;
    String Title;
    String Message;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            Listener = (AddDialogListener) context;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setInfo(String Title,String Message){
        this.Title = Title;
        this.Message = Message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Title).setMessage(Message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Listener.onYesClicked();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        return  builder.create();
    }
    public interface AddDialogListener{
        void onYesClicked();
    }
}
