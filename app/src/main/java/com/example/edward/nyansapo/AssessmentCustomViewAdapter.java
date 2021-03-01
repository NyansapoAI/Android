package com.example.edward.nyansapo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AssessmentCustomViewAdapter extends  RecyclerView.Adapter<AssessmentCustomViewAdapter.MyViewHolder>  {


    private Context context;
    private ArrayList<Assessment> assessments;
    private OnAssessmentListener mOnAssessmentListener;

    AssessmentCustomViewAdapter(Context context, ArrayList<Assessment> assessments, OnAssessmentListener onAssessmentListener){
        this.context = context;
        this.assessments = assessments;
        this.mOnAssessmentListener = onAssessmentListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.assessment_row, viewGroup ,false);
        return new MyViewHolder(view, mOnAssessmentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.name_view.setText("Assessment "+Integer.toString(i + 1));
        myViewHolder.level_view.setText(getLevelKey(assessments.get(i).getLEARNING_LEVEL()));
        myViewHolder.timestamp_view.setText(assessments.get(i).getTIMESTAMP().split("GMT")[0]);

    }


    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name_view, timestamp_view, level_view;

        AssessmentCustomViewAdapter.OnAssessmentListener onAssessmentListener;
        public MyViewHolder(@NonNull View itemView, OnAssessmentListener onAssessmentListener){
            super(itemView);
            name_view = itemView.findViewById(R.id.name_view);
            timestamp_view = itemView.findViewById(R.id.timestamp_view);
            level_view = itemView.findViewById(R.id.level_view);
            this.onAssessmentListener = onAssessmentListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnAssessmentListener.OnAssessmentClick(getAdapterPosition());
        }
    }

    public String getLevelKey(String level){
        switch (level){
            case "LETTER": return "L";
            case "WORD" : return "w";
            case "STORY" : return "S";
            case "PARAGRAPH": return "P";
            case "ABOVE" : return "A";
            default: return "U";
        }
    }

    public interface OnAssessmentListener{
        void OnAssessmentClick(int position);
    }

    public interface OnStudentListener{
        void OnStudentClick(int position);
    }

}
