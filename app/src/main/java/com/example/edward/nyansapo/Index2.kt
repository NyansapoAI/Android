package com.example.edward.nyansapo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_index.*

class index2 : AppCompatActivity() {
    // declare database connection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        logoutUser()

        logo.setOnClickListener { checkUser() }
         checkUser()

    }

    private fun logoutUser() {

        AuthUI.getInstance().signOut(this).addOnSuccessListener {

        }
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