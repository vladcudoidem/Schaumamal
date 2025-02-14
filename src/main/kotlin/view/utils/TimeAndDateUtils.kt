package view.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getFormattedDate(millis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
    return dateFormat.format(calendar.time)
}