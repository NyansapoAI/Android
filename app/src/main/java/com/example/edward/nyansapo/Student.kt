package com.example.edward.nyansapo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Student (
    var local_id: String? = null,
    var cloud_id: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var age: String? = null,
    var gender: String? = null,
    var notes: String? = null,
    var timestamp: String? = null,
    var instructor_id: String? = null,
    var learningLevel: String? = null,
    var std_class: String? = null
):Parcelable