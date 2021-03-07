package com.example.edward.nyansapo.presentation

import android.app.Application
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application() {




    override fun onCreate() {
        super.onCreate()
    }
}
var documentSnapshot:DocumentSnapshot?=null
