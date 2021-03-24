package com.example.edward.nyansapo.presentation.ui.activities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Activity
(
 val name: String,
 val level: String,
 val id: String,
 val learningObjectives: String,
 val steps: String,
 val materials: String,
 ) : Parcelable
