package com.example.firebaseWithRoom.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.fromLongToDDMMMYYYY(): String {
    val outputFormat = SimpleDateFormat("dd-MMM-yyyy,HH:mm", Locale.ENGLISH)
    val date = Date(this)
    return outputFormat.format(date)
}