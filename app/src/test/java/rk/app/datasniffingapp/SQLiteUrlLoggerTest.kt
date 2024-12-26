package rk.app.datasniffingapp

import android.database.sqlite.SQLiteDatabase
import org.junit.Test
import rk.app.datasniffingapp.data.logger.SQLiteUrlLogger
import java.io.File

class SQLiteUrlLoggerTest {

    @Test
    fun `logUrl inserts correct data into SQLite`() {
        val tempDbFile = File.createTempFile("test", ".db")
        val logger = SQLiteUrlLogger(tempDbFile.absolutePath)

        val packageName = "com.example.app"
        val url = "http://example.com"
        val hookType = "Okhttp"
        logger.logUrl(packageName, url, hookType)

        val db =
            SQLiteDatabase.openDatabase(tempDbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = db.rawQuery("SELECT packageName, url FROM UrlLogs", null)

        assert(cursor.moveToFirst())
        assert(cursor.getString(0) == packageName)
        assert(cursor.getString(1) == url)

        cursor.close()
        db.close()
        tempDbFile.delete()
    }
}
