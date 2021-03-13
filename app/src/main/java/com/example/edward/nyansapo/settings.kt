package com.example.edward.nyansapo


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.edward.nyansapo.MainActivity
import com.example.edward.nyansapo.home
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_settings.*

class settings : AppCompatActivity() {
      companion object {
              private  const val TAG="settings"
          }

    var firstname: TextView? = null
    var lastname: TextView? = null
    var email: TextView? = null
    var password: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { //startActivity(new Intent(getApplicationContext(), home.class));
            val myIntent = Intent(baseContext, home::class.java)
            startActivity(myIntent)
        }


        // ui code
         firstname = findViewById(R.id.firstname)
        lastname = findViewById(R.id.lastname)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        update_button.setOnClickListener(View.OnClickListener { //updateInfo();
            Toast.makeText(this@settings, "Under Development", Toast.LENGTH_LONG).show()
        })
        logout_button.setOnClickListener(View.OnClickListener {
            logout()
          })
        setInstructorInfo()
    }

    private fun logout() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {

            Log.d(TAG, "onNavigationItemSelected: logout success")
            val myIntent = Intent(baseContext, MainActivity::class.java)
            startActivity(myIntent)

        }
    }

    fun setInstructorInfo() {

        //Get instructor
          FirebaseUtils.getCurrentUser {

             email!!.text = FirebaseUtils.firebaseAuth.currentUser?.email
              firstname!!.text = FirebaseUtils.firebaseAuth.currentUser?.displayName
              lastname!!.text =  FirebaseUtils.firebaseAuth.currentUser?.displayName

          }
        //Instructor ins = databasehelper.getInstructorByEmail("edward@kijenzi.com");

        //Toast.makeText(this, instructor.getCloud_id(), Toast.LENGTH_LONG).show();

        //Toast.makeText(this, ins.getLocal_id(), Toast.LENGTH_LONG).show();

        // set Text to screen

    }

    fun updateInfo() {

        // check inputs
        assert(firstname!!.text === "")
        assert(lastname!!.text === "")
        assert(email!!.text === "")
        assert(password!!.text === "")

        /*
{
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImVkd2FyZEAxa2lqZW56aS5jb20iLCJ1c2VySWQiOiI1Zjg1YWY4YzJkYjM4MzI3N2FkOWU0NTkiLCJpYXQiOjE2MDI2NjgwNTcsImV4cCI6MTYwMjY3MTY1N30.0zijIUm8nllFVhAT41P_8mVGBoh5VsL5xyu2Z9fI_Q8",
            "updates":
                    [
                        {"firstname": "Mumbe"},
                            {"password":"1234"}
                ]
}
         */
    }
}