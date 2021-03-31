package com.nyansapo.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_recording_table")
data class AssessmentRecording(
        @PrimaryKey(autoGenerate = false)
        var id: String="",
        var story: String="",
        var paragraph0: String="",
        var paragraph1: String="",
        var paragraph2: String="",
        var paragraph3: String="",
        var word0: String = "",
        var word1: String = "",
        var word2: String = "",
        var word3: String = "",
        var word4: String = "",
        var word5: String = "",
        var letter0: String = "",
        var letter1: String = "",
        var letter2: String = "",
        var letter3: String = "",
        var letter4: String = "",
        var letter5: String = "",

        )

