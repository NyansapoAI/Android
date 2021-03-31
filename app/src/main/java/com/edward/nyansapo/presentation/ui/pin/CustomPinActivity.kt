package com.nyansapo.presentation.ui.pin

import android.graphics.Color
import android.util.Log
import android.widget.EditText
import com.nyansapo.R
import com.nyansapo.presentation.ui.main.MainActivity2
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity
import com.github.omadahealth.lollipin.lib.managers.LockManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty

class CustomPinActivity : AppLockActivity() {


    lateinit var edittext: EditText
    override fun showForgotDialog() {
        edittext = EditText(MainActivity2.activityContext!!)
        edittext!!.setTextColor(Color.BLACK)


        MaterialAlertDialogBuilder(this).setBackground(this.getDrawable(R.drawable.button_first)).setView(edittext).setTitle("Forgot Password").setMessage("Please Enter The Name Of The Program Manager").setNegativeButton("Cancel") { dialog, which -> onCancel() }.setPositiveButton("Submit") { dialog, which -> onSubmit() }.show()


    }

    private fun onCancel() {
    }

    private fun onSubmit() {
        Log.d(TAG, "onSubmit: clicked: :${edittext.text.toString()}")
        if (edittext!!.text.toString().trim().equals("mumbe", ignoreCase = true)) {
            Log.d(TAG, "onSubmit: correct")
            LockManager.getInstance().disableAppLock()
            Toasty.info(this, "Lock Disabled").show()
            finish()

        } else {
            Log.d(TAG, "onSubmit: wrong")
            Toasty.error(this, "You got the quiz wrong").show()

        }
    }

    override fun onPinFailure(attempts: Int) {}
    override fun onPinSuccess(attempts: Int) {}
    override fun getPinLength(): Int {

        return super.getPinLength() //you can override this method to change the pin length from the default 4
    }


}