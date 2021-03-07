package com.example.edward.nyansapo.add_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

class AddDialog2 : AppCompatDialogFragment() {
    private var Listener: AddDialogListener? = null
    var Title: String? = null
    var Message: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            Listener = context as AddDialogListener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setInfo(Title: String?, Message: String?) {
        this.Title = Title
        this.Message = Message
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(Title).setMessage(Message)
                .setPositiveButton("Yes") { dialogInterface, i -> Listener!!.onYesClicked() }.setNegativeButton("No") { dialogInterface, i -> }
        return builder.create()
    }

    interface AddDialogListener {
        fun onYesClicked()
    }
}