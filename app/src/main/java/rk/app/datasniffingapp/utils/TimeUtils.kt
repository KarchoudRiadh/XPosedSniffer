package rk.app.datasniffingapp.utils

import java.text.SimpleDateFormat
import java.util.Date

fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd - HH:mm:ss", java.util.Locale.getDefault())
    return dateFormat.format(Date())
}