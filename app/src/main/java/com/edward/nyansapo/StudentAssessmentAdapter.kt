package com.nyansapo

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nyansapo.databinding.AssessmentRowNormalBinding
import com.nyansapo.presentation.utils.assessmentDocumentSnapshot
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty

class StudentAssessmentAdapter(private val studentAssessments: Activity, options: FirestoreRecyclerOptions<Assessment?>, val onAssessmentClick: (Assessment) -> Unit) : FirestoreRecyclerAdapter<Assessment, StudentAssessmentAdapter.ViewHolder>(options) {

    companion object {
            private  const val TAG="StudentAssessmentAdapte"
        }

    private val context: Context? = studentAssessments
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Assessment) {


        holder.binding.apply {
            setOnClickListeners(holder, position)
            assessmentBtn.setText("Assessment " + Integer.toString(itemCount - position))
         //   assessmentBtn.setText("Assessment " + Integer.toString(itemCount - position) + " :${model.learningLevel}")


        }


    }

    private fun setOnClickListeners(holder: ViewHolder, position: Int) {


        holder.binding.assessmentBtn.setOnClickListener {
            Log.d(TAG, "setOnClickListeners: ")
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            assessmentDocumentSnapshot = snapshots.getSnapshot(position)
            onAssessmentClick(getItem(position))
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.assessment_row_normal, parent, false)
        val binding: AssessmentRowNormalBinding = AssessmentRowNormalBinding.bind(view)

        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: AssessmentRowNormalBinding) : RecyclerView.ViewHolder(binding.root) {



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

