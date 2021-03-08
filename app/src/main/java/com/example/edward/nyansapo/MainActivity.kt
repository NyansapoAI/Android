package com.example.edward.nyansapo

import android.Manifest.permission
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edward.nyansapo.presentation.ui.login.LoginActivity.Companion.RC_SIGN_IN
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import es.dmoral.toasty.Toasty
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // declare view
    var user_email: EditText? = null
    var user_password: EditText? = null
    var Token = ""
    lateinit var loginAnimation: Animation
    var rotating_icon: ImageView? = null
    var signin: Button? = null
    var signup: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfUserIsLoggedIn()

        user_email = findViewById(R.id.email)
        user_password = findViewById(R.id.password)
        val requestCode = 5 // unique code for the permission request
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission.RECORD_AUDIO, permission.INTERNET), requestCode)
        loginAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        loginAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        rotating_icon = findViewById(R.id.rotating_icon)
        signin = findViewById(R.id.signin)
        signup = findViewById(R.id.signup)
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
        FirebaseUtils.isInstructorSetUp { flag->
            if (flag==true){

                goToHomePage()

            }else{
                registerInstructor()

            }
        }
    }

    private fun goToHomePage() {
        val myIntent = Intent(baseContext, home::class.java)
        startActivity(myIntent)

    }

    private fun showToast(message: String) {
        Toasty.error(this, message).show()
    }

    fun startSelector(v: View?) {
        //login("edward@kijenzi.com", "nyansapo");
        rotating_icon!!.visibility = View.VISIBLE
        rotating_icon!!.startAnimation(loginAnimation)
        //email.setEnabled(false);
        //password.setEnabled(false);
        signin!!.isEnabled = false
        signup!!.isEnabled = false
    }

    fun registerInstructor() {
        val myIntent = Intent(baseContext, RegisterTeacher::class.java)
        startActivity(myIntent)
    }


}