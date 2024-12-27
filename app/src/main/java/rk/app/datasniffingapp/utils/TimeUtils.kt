package rk.app.datasniffingapp.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Returns the current timestamp formatted as a string.
 *
 * This function generates the current date and time in the format "yyyy-MM-dd - HH:mm:ss",
 * using the default locale of the system. It can be useful for logging or timestamping events.
 *
 * @return A string representing the current timestamp in the format "yyyy-MM-dd - HH:mm:ss".
 */
fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd - HH:mm:ss", java.util.Locale.getDefault())
    return dateFormat.format(Date())
}