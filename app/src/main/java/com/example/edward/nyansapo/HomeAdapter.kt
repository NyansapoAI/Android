package com.example.edward.nyansapo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.databinding.StudentRowBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import es.dmoral.toasty.Toasty

class HomeAdapter(private val home: home, options: FirestoreRecyclerOptions<Student?>,  val onStudentClick: ()->Unit) : FirestoreRecyclerAdapter<Student, HomeAdapter.ViewHolder>(options) {
    private val context: Context? = home
    private lateinit var currentSnapshot: DocumentSnapshot
    private val parentData: Student? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Student) {


        holder.binding.apply {


            holder.binding.apply {

                nameView.setText(model.getFirstname() + " " + model.getLastname())
                ageView.setText("Age: " + model.getAge())
                genderView.setText("Gender: " + model.getGender())
                levelView.setText(model.getLearning_level())
                levelView.setText(getLevelKey(model.getLearning_level()))
                classView.setText("Class: " + model.getStd_class())

            }

        }
        setOnClickListeners(holder, position)
    }

    private fun setOnClickListeners(holder: ViewHolder, position: Int) {


        holder.itemView.setOnClickListener(View.OnClickListener {
            if (position == RecyclerView.NO_POSITION) {
                return@OnClickListener
            }
            Constants.studentDocumentSnapshot=snapshots.getSnapshot(position)
            onStudentClick()
        })
    }

    fun deleteFromDatabase(position: Int) {
        MaterialAlertDialogBuilder(context!!).setBackground(context.getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_delete).setTitle("delete").setMessage("Are you sure you want to delete ").setNegativeButton("no") { dialog, which -> notifyItemChanged(position) }.setPositiveButton("yes") { dialog, which -> deleteData(position) }.show()
    }

    private fun deleteData(position: Int) {
        currentSnapshot = snapshots.getSnapshot(position)
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

    class ViewHolder(val binding: StudentRowBinding) : RecyclerView.ViewHolder(binding.root) {

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

