package com.nyansapo.presentation.ui.attendance

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class CurrentDate(@ServerTimestamp val date:Date?=null) {
}