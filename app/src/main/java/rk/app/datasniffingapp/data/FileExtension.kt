package rk.app.datasniffingapp.data

/**
 * Enum class representing different file extensions for log files.
 *
 * Each enum constant corresponds to a specific file type and its associated separator
 * that is used when logging data. The separator is applied to delimit the fields in the log files.
 *
 * @property separator The character used to separate values in the log file of the respective type.
 */
enum class FileExtension(val separator: String) {
    CSV(";"), TXT("*"), DB("")
}