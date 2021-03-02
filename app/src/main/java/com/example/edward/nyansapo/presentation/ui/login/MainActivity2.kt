package com.example.edward.nyansapo.presentation.ui.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity2 : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() {
        Log.d(TAG, "checkIfUserIsLoggedIn: checking if user is logged in")
        if (firebaseAuth.currentUser == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.logo_wrapper)
                            .setAvailableProviders(Arrays.asList(
                                    GoogleBuilder().build(),
                                    EmailBuilder().build(),
                                    PhoneBuilder().build() //     new   AuthUI.IdpConfig.AnonymousBuilder().build()
                            ))
                            .build(),
                    RC_SIGN_IN)
        } else {

            Log.d(TAG, "checkIfUserIsLoggedIn: user already logged in")
        }
    }

    companion object {
        private const val TAG = "MainActivity2"
        const val RC_SIGN_IN = 3
    }
}