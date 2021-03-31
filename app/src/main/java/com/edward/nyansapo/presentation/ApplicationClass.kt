package com.nyansapo.presentation

import android.app.Application
import com.nyansapo.presentation.ui.pin.CustomPinActivity
import com.github.omadahealth.lollipin.lib.managers.LockManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application() {



    override fun onCreate() {
        super.onCreate()

   //     lockScreenSetup()
    }

    private fun lockScreenSetup() {
        val lockManager = LockManager.getInstance()

        lockManager.enableAppLock(this, CustomPinActivity::class.java)
        lockManager.appLock.logoId = -1
        lockManager.appLock.timeout = 10000    //seconds to timeout

    }
}

