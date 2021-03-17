package com.example.edward.nyansapo.presentation.ui.attendance

data class StudentAttendance (val name:String,val present:Boolean){
    constructor():this("",true)
}