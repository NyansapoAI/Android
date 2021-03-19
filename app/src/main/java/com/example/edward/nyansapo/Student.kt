package com.example.edward.nyansapo

import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Student(
        @DocumentId
        val id: String? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        var age: String? = null,
        var gender: String? = null,
        var notes: String? = null,

        @ServerTimestamp
        var timestamp: Date? = null,
        var instructor_id: String? = null,
        var learningLevel: String? = Learning_Level.UNKNOWN.name,
        var std_class: String? = null
) : Parcelable, SearchSuggestion {

    override fun getBody(): String {
        return "$firstname" + " $lastname"
    }
}