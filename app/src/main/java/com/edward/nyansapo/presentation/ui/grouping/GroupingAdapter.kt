package com.nyansapo.presentation.ui.grouping

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nyansapo.R
import com.nyansapo.Student
import com.nyansapo.databinding.ItemStudentBinding
import com.nyansapo.presentation.ui.main.MainActivity2
import com.nyansapo.presentation.utils.studentDocumentSnapshot
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import es.dmoral.toasty.Toasty

class GroupingAdapter(fragment: GroupingFragment, options: FirestoreRecyclerOptions<Student?>, val onStudentClick: (DocumentSnapshot) -> Unit, val onStudentLongClicked: (DocumentSnapshot) -> Unit) : FirestoreRecyclerAdapter<Student, GroupingAdapter.ViewHolder>(options) {

    private val TAG = "LearningLevelAdapter"


    private val context: Context? = MainActivity2.activityContext!!
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Student) {


        holder.binding.apply {

            Log.d(TAG, "onBindViewHolder: " + model.firstname + " " + model.lastname)

            setOnClickListeners(holder, position)

            nameTxtView.setText(model.firstname + " " + model.lastname)


        }


    }

    private fun setOnClickListeners(holder: ViewHolder, position: Int) {


        holder.itemView.setOnClickListener {
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            studentDocumentSnapshot = snapshots.getSnapshot(position)
            onStudentClick(snapshots.getSnapshot(position))
        }
        holder.itemView.setOnLongClickListener {
            if (position == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener false
            }
            studentDocumentSnapshot = snapshots.getSnapshot(position)
            onStudentLongClicked(snapshots.getSnapshot(position))

            true
        }




    }

    fun deleteFromDatabase(position: Int) {
        MaterialAlertDialogBuilder(context!!).setBackground(context.getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_delete).setTitle("delete").setMessage("Are you sure you want to delete ").setNegativeButton("no") { dialog, which -> notifyItemChanged(position) }.setPositiveButton("yes") { dialog, which -> deleteData(position) }.show()
    }

    private fun deleteData(position: Int) {
        val currentSnapshot = snapshots.getSnapshot(position)

        currentSnapshot.reference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toasty.success(context!!, "Deletion Success", Toast.LENGTH_SHORT).show()
            } else {
                val error = task.exception!!.message
                Toasty.error(context!!, "Error: $error", Toast.LENGTH_SHORT).show()
            }
          }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        val binding: ItemStudentBinding = ItemStudentBinding.bind(view)

        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {


    }




}

