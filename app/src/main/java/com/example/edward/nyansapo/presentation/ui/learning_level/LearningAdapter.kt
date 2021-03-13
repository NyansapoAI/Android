package com.example.edward.nyansapo.presentation.ui.learning_level

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.ItemStudentBinding
import com.example.edward.nyansapo.db.models.Student2
import kotlinx.android.synthetic.main.item_student.view.*

class LearningAdapter : ListAdapter<Student, RecyclerView.ViewHolder>(DIFF_UTIL) {


    inner class ViewHolder(val binding:ItemStudentBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
     val view= LayoutInflater.from(parent.context).inflate(R.layout.item_student,parent,false)

        val binding:ItemStudentBinding= ItemStudentBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       holder.itemView.nameTxtView.text=getItem(position).firstname +" "+ getItem(position).lastname
    }

    companion object {
         val DIFF_UTIL=object: ItemCallback<Student>(){
                override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
                    TODO("Not yet implemented")
                }

                override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
                    TODO("Not yet implemented")
                }
            }
    }

}