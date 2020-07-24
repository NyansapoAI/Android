package com.example.edward.nyansapo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomViewAdapter extends RecyclerView.Adapter<CustomViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Student> students;

    CustomViewAdapter(Context context, ArrayList<Student> students){
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_row, viewGroup ,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.name_view.setText(students.get(i).getFirstname() +" "+ students.get(i).getLastname());
        myViewHolder.age_view.setText("Age: "+students.get(i).getAge());
        myViewHolder.gender_view.setText("Gender: "+students.get(i).getGender());
        //myViewHolder.level_view.setText(students.get(i).getLearning_level());
        myViewHolder.level_view.setText("L");
        myViewHolder.class_view.setText("Class: 5");
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name_view, class_view, age_view, gender_view, level_view;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            name_view = itemView.findViewById(R.id.name_view);
            class_view = itemView.findViewById(R.id.class_view);
            age_view = itemView.findViewById(R.id.age_view);
            gender_view = itemView.findViewById(R.id.gender_view);
            level_view = itemView.findViewById(R.id.level_view);
            imageView =  itemView.findViewById(R.id.image_view);
        }

    }
}
