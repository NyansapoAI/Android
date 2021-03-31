package com.nyansapo.presentation.ui.attendance

data class StudentAttendance (val name:String,val present:Boolean=true){
    constructor():this("",true)
}