package com.example.edward.nyansapo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SelectAssessmentModal extends BottomSheetDialogFragment {

    private AssessmentModalListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_layout, container, false);

        Button assessment3 = v.findViewById(R.id.assessment3_button);
        Button assessment4 = v.findViewById(R.id.assessment4_button);
        Button assessment5 = v.findViewById(R.id.assessment5_button);

        assessment3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("assessment_3");
                dismiss();
            }
        });

        assessment4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("assessment_4");
                dismiss();
            }
        });

        assessment5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("assessment_5");
                dismiss();
            }
        });

        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public interface AssessmentModalListener {
        void onButtonClicked(String text);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AssessmentModalListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+ " must implement AssessmentModalListener");
        }

    }
}
