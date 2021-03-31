package com.nyansapo.presentation.utils

import com.nyansapo.R
import com.nyansapo.db.AssessmentRecording

class GlobalData {
    companion object {
        @JvmField
        var avatar: Int = R.drawable.nyansapo_avatar_lion
    @JvmField
        var assessmentRecording: AssessmentRecording = AssessmentRecording()
    }
}