package com.example.edward.nyansapo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.ui.login.LoginActivity.Companion.RC_SIGN_IN
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import es.dmoral.toasty.Toasty
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfUserIsLoggedIn()


    }

    private fun checkIfUserIsLoggedIn() {
        Log.d(TAG, "checkIfUserIsLoggedIn: checking if user is logged in")
        if (!FirebaseUtils.isLoggedIn) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    AuthUI.IdpConfig.EmailBuilder().build(),
                            ))
                            .build(),
                    RC_SIGN_IN)
        } else {
            Log.d(TAG, "checkIfUserIsLoggedIn: user already logged in")

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                //is sign in is success we want to recreate the activity
                Log.d(TAG, "onActivityResult: success sign in")

                successLoggedIn()

            } else {
                // Sign in failed
                Log.d(TAG, "onActivityResult: sign in failed")
                if (response == null) {
                    // User pressed back button
                    showToast("sign in cancelled")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    showToast("not internet connection")
                    return
                }
                showToast("unknown error")
                Log.e(TAG, "Sign-in error: ", response.error)
            }
        }
    }

    private fun successLoggedIn() {

      //  goToSetPattern()

        goToHomePage()



/*        FirebaseUtils.isInstructorSetUp { flag->
            if (flag==true){

                goToHomePage()

            }else{
                registerInstructor()

            }
        }*/
    }



    private fun goToHomePage() {
        val myIntent = Intent(baseContext, home::class.java)
        startActivity(myIntent)

    }

    private fun showToast(message: String) {
        Toasty.error(this, message).show()
    }


    fun registerInstructor() {
        val myIntent = Intent(baseContext, RegisterTeacher::class.java)
        startActivity(myIntent)
    }


}