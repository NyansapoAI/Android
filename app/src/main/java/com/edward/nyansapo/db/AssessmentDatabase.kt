package com.nyansapo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nyansapo.Assessment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [AssessmentRecording::class], version = 3)
abstract class AssessmentDatabase : RoomDatabase() {

    abstract fun assessmentDao(): AssessmentDao


    }
