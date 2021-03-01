package com.example.edward.nyansapo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomViewAdapter extends RecyclerView.Adapter<CustomViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Student> students;
    private OnStudentListener mOnStudentListener;

    CustomViewAdapter(Context context, ArrayList<Student> students, OnStudentListener onStudentListener){
        this.context = context;
        this.students = students;
        this.mOnStudentListener = onStudentListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_row, viewGroup ,false);
        return new MyViewHolder(view, mOnStudentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.name_view.setText(students.get(i).getFirstname() +" "+ students.get(i).getLastname());
        myViewHolder.age_view.setText("Age: "+students.get(i).getAge());
        myViewHolder.gender_view.setText("Gender: "+students.get(i).getGender());
        myViewHolder.level_view.setText(students.get(i).getLearning_level());
        myViewHolder.level_view.setText(getLevelKey(students.get(i).getLearning_level()));
        myViewHolder.class_view.setText("Class: " + students.get(i).getStd_class());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name_view, class_view, age_view, gender_view, level_view;

        OnStudentListener onStudentListener;
        public MyViewHolder(@NonNull View itemView, OnStudentListener onStudentListener){
            super(itemView);
            name_view = itemView.findViewById(R.id.name_view);
            class_view = itemView.findViewById(R.id.class_view);
            age_view = itemView.findViewById(R.id.age_view);
            gender_view = itemView.findViewById(R.id.gender_view);
            level_view = itemView.findViewById(R.id.level_view);
            this.onStudentListener = onStudentListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStudentListener.OnStudentClick(getAdapterPosition());
        }
    }

    public String getLevelKey(String level){
        switch (level){
            case "LETTER": return "L";
            case "WORD" : return "w";
            case "STORY" : return "S";
            case "PARAGRAPH": return "P";
            case "ABOVE" : return "C";
            default: return "U";
        }
    }

    public interface OnStudentListener{
        void OnStudentClick(int position);
    }
}
