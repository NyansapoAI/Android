package com.example.edward.nyansapo.presentation.ui.attendance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.ItemAttendanceBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class AttendanceAdapter(options: FirestoreRecyclerOptions<StudentAttendance?>, val onStudentChecked: (documentSnapshot: DocumentSnapshot, isChecked: Boolean) -> Unit) : FirestoreRecyclerAdapter<StudentAttendance, AttendanceAdapter.ViewHolder>(options) {

    private val TAG = "AttendanceAdapter"

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: StudentAttendance) {


        holder.binding.apply {


            holder.binding.apply {

                nameTxtView.setText(model.name)
                attendanceCheckbox.isChecked = model.present

            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        val binding: ItemAttendanceBinding = ItemAttendanceBinding.bind(view)

        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.attendanceCheckbox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                    val student = getItem(bindingAdapterPosition)
                    if (student.present == isChecked) {
                        Log.d(TAG, "onCheckedChanged: no need to change")
                    } else {
                        onStudentChecked(snapshots.getSnapshot(bindingAdapterPosition), isChecked)
                    }


                }
            })
        }

    }


}

