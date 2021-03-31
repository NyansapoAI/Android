package com.nyansapo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nyansapo.databinding.StudentRowBinding
import com.nyansapo.presentation.utils.studentDocumentSnapshot
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import es.dmoral.toasty.Toasty

class HomeAdapter(private val home: home, options: FirestoreRecyclerOptions<Student?>,  val onStudentClick: (documentSnapshot:DocumentSnapshot)->Unit) : FirestoreRecyclerAdapter<Student, HomeAdapter.ViewHolder>(options) {
    private val context: Context? = home
     override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Student) {


         holder.binding.apply {


             nameView.setText(model.firstname + " " + model.lastname)
             ageView.setText("Age: " + model.age)
             genderView.setText("Gender: " + model.gender)
             classView.setText("Class: " + model.std_class)

                 levelView.setText(getLevelKey(model.learningLevel))


         }
     }

    private fun setOnClickListeners(holder: ViewHolder, position: Int) {


        holder.itemView.setOnClickListener {
            if (position == RecyclerView.NO_POSITION) {
               return@setOnClickListener
            }
            studentDocumentSnapshot=snapshots.getSnapshot(position)
            onStudentClick(snapshots.getSnapshot(position))
        }
    }

    fun deleteFromDatabase(position: Int) {
        MaterialAlertDialogBuilder(context!!).setBackground(context.getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_delete).setTitle("delete").setMessage("Are you sure you want to delete ").setNegativeButton("no") { dialog, which -> notifyItemChanged(position) }.setPositiveButton("yes") { dialog, which -> deleteData(position) }.show()
    }

    private fun deleteData(position: Int) {
        val currentSnapshot = snapshots.getSnapshot(position)
        home.showProgress(true)

        currentSnapshot.reference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toasty.success(context!!, "Deletion Success", Toast.LENGTH_SHORT).show()
            } else {
                val error = task.exception!!.message
                Toasty.error(context!!, "Error: $error", Toast.LENGTH_SHORT).show()
            }
            home.showProgress(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_row, parent, false)
        val binding: StudentRowBinding = StudentRowBinding.bind(view)

        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: StudentRowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            setOnClickListeners(this, bindingAdapterPosition)

        }
    }

    fun getLevelKey(level: String?): String? {
        return when (level) {
            "LETTER" -> "L"
            "WORD" -> "w"
            "STORY" -> "S"
            "PARAGRAPH" -> "P"
            "ABOVE" -> "C"
            else -> "U"
        }
    }



}

