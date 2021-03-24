package com.example.edward.nyansapo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.ui.login.LoginActivity
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_index.*
import java.util.*

class index2 : AppCompatActivity() {
    // declare database connection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)


     checkIfUserIsLoggedIn()
    }

    private fun logoutUser() {

        AuthUI.getInstance().signOut(this).addOnSuccessListener {

        }
    }
    private fun checkIfUserIsLoggedIn() {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.logo_wrapper)
                            .setAvailableProviders(Arrays.asList(
                                 //   AuthUI.IdpConfig.GoogleBuilder().build(),
                                    AuthUI.IdpConfig.EmailBuilder().build(),
                               //     AuthUI.IdpConfig.PhoneBuilder().build() //     new   AuthUI.IdpConfig.AnonymousBuilder().build()
                            ))
                            .build(),
                    LoginActivity.RC_SIGN_IN)

    }

    private fun checkUser() {

        //Toast.makeText(index.this, instructor_id, Toast.LENGTH_SHORT).show();
        if (FirebaseUtils.isLoggedIn) {
            //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            val myIntent = Intent(baseContext, home::class.java)
            startActivity(myIntent)
        } else {
            //Toast.makeText(this, "Sign", Toast.LENGTH_SHORT).show();
            val myIntent = Intent(baseContext, MainActivity::class.java)
            //myIntent.putExtra("instructor_id", instructor_id); // its id for now
            startActivity(myIntent)
        }
    }
}