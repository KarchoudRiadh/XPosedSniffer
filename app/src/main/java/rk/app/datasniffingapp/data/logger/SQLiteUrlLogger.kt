package rk.app.datasniffingapp.data.logger

import android.database.sqlite.SQLiteDatabase
import de.robv.android.xposed.XposedBridge
import rk.app.datasniffingapp.domain.logger.UrlLogger
import java.io.File

/**
 * Logs URLs to an SQLite database.
 *
 * @param databasePath The path of the SQLite database to store logs.
 */
class SQLiteUrlLogger(private val databasePath: String) : UrlLogger {

    init {
        try {
            val parentDir = File(databasePath).parentFile
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    XposedBridge.log("[SQLiteUrlLogger] Failed to create parent directories for: $parentDir")
                }
            } else {
                val db = SQLiteDatabase.openOrCreateDatabase(databasePath, null)
                db.execSQL("CREATE TABLE IF NOT EXISTS UrlLogs (packageName TEXT, url TEXT, hookType TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)")
                db.close()
                XposedBridge.log("[SQLiteUrlLogger] Database initialized at: $databasePath")
            }
        } catch (e: Exception) {
            XposedBridge.log("[SQLiteUrlLogger] Error initializing database: ${e.message} for $databasePath")
        }
    }

    /**
     * Logs a URL along with its associated package name and hook type into an SQLite database.
     *
     * @param packageName The package name of the app for which the URL is being logged.
     * @param url The URL to be logged. This can include special characters such as ":".
     * @param hookType The type of hook that triggered the logging (e.g., API hook, HTTP hook).
     */
    override fun logUrl(packageName: String, url: String, hookType: String) {
        try {
            val db = SQLiteDatabase.openOrCreateDatabase(databasePath, null)
            db.compileStatement("INSERT INTO UrlLogs (packageName, url, hookType) VALUES (?, ?, ?)")
                .apply {
                    bindString(1, packageName)
                    bindString(2, url)
                    bindString(3, hookType)
                    executeInsert()
                }
            db.close()
            XposedBridge.log("[SQLiteUrlLogger] URL inserted to database: $url")
        } catch (e: Exception) {
            XposedBridge.log("[SQLiteUrlLogger] Error logging URL to database: ${e.message} for $databasePath")
        }
    }
}
