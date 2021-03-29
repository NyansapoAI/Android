package com.example.edward.nyansapo


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Interface_selecter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interface_selecter)
    }

    /*
    public void startTeacherInterface(View v){
        Intent myIntent = new Intent(getBaseContext(), Begin_Assessment.class);
        startActivity(myIntent);
    }
*/
    fun startStudentInterface(v: View?) {
        val myIntent = Intent(baseContext, AddStudentFragment::class.java)
        startActivity(myIntent)
    }

    fun goHome(v: View?) {
        val myIntent = Intent(baseContext, MainActivity::class.java)
        startActivity(myIntent)
    }
}