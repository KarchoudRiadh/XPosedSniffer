package rk.app.datasniffingapp.data.logger

import de.robv.android.xposed.XposedBridge
import rk.app.datasniffingapp.domain.logger.UrlLogger
import rk.app.datasniffingapp.utils.getCurrentTimestamp
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Logs URLs to a given file type.
 *
 * @param filePath The path of the file to store logs.
 * @param fileExtension the file extension to use for the log file.
 */
class FileUrlLogger(
    private val filePath: String,
    private val fileExtension: FileExtension
) : UrlLogger {
    override fun logUrl(packageName: String, url: String, hookType: String) {
        try {
            val parentDir = File(filePath).parentFile
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    XposedBridge.log("[FileUrlLogger] Failed to create parent directories for: $parentDir")
                }
            } else {
                val file = File(filePath)
                if (!file.exists()) file.createNewFile()
                FileWriter(file, true).use { writer ->
                    val newLine = listOf(
                        getCurrentTimestamp(),
                        packageName,
                        hookType,
                        url
                    ).joinToString(separator = fileExtension.separator)
                    writer.appendLine(newLine)
                }
                XposedBridge.log("[FileUrlLogger] $hookType - URL : $url => logged to ${file.canonicalPath + fileExtension.separator}")
            }
        } catch (e: IOException) {
            XposedBridge.log("[FileUrlLogger] Error logging URL: ${e.message} for $filePath")
        }
    }
}

