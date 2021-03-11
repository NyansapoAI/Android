package com.example.edward.nyansapo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.edward.nyansapo.databinding.AssessmentRowBinding
import com.example.edward.nyansapo.presentation.utils.Constants
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import es.dmoral.toasty.Toasty
import java.text.DateFormat

class StudentAssessmentAdapter(private val studentAssessments: student_assessments, options: FirestoreRecyclerOptions<Assessment?>, val onAssessmentClick: (Assessment) -> Unit) : FirestoreRecyclerAdapter<Assessment, StudentAssessmentAdapter.ViewHolder>(options) {

    companion object {
            private  const val TAG="StudentAssessmentAdapte"
        }

    private val context: Context? = studentAssessments
    private lateinit var currentSnapshot: DocumentSnapshot
    private val parentData: Student? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Assessment) {


        holder.binding.apply {


            holder.binding.apply {

                nameView.setText("Assessment " + Integer.toString(position + 1))
                levelView.setText(getLevelKey(model.learningLevel))


                try {
                    val date=DateFormat.getDateTimeInstance().format(model.timestamp)
                    timestampView.setText(date)

                }catch (e:Exception){
                    Log.e(TAG, "onBindViewHolder: ${e.message}",e )
                }

            }

        }
        setOnClickListeners(holder, position)
    }

    private fun setOnClickListeners(holder: ViewHolder, position: Int) {


        holder.itemView.setOnClickListener(View.OnClickListener {
            if (position == RecyclerView.NO_POSITION) {
                return@OnClickListener
            }
            Constants.assessmentDocumentSnapshot = snapshots.getSnapshot(position)
            onAssessmentClick(getItem(position))
        })
    }

    fun deleteFromDatabase(position: Int) {
        MaterialAlertDialogBuilder(context!!).setBackground(context.getDrawable(R.drawable.button_first)).setIcon(R.drawable.ic_delete).setTitle("delete").setMessage("Are you sure you want to delete ").setNegativeButton("no") { dialog, which -> notifyItemChanged(position) }.setPositiveButton("yes") { dialog, which -> deleteData(position) }.show()
    }

    private fun deleteData(position: Int) {
        currentSnapshot = snapshots.getSnapshot(position)
        studentAssessments.showProgress(true)

        currentSnapshot.reference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toasty.success(context!!, "Deletion Success", Toast.LENGTH_SHORT).show()
            } else {
                val error = task.exception!!.message
                Toasty.error(context!!, "Error: $error", Toast.LENGTH_SHORT).show()
            }
            studentAssessments.showProgress(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.assessment_row, parent, false)
        val binding: AssessmentRowBinding = AssessmentRowBinding.bind(view)

        return ViewHolder(binding)
    }

    class ViewHolder(val binding: AssessmentRowBinding) : RecyclerView.ViewHolder(binding.root) {

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

