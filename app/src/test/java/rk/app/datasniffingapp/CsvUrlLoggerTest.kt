package rk.app.datasniffingapp

import org.junit.Test
import rk.app.datasniffingapp.data.FileExtension
import rk.app.datasniffingapp.data.FileUrlLogger
import java.io.File

class CsvUrlLoggerTest {

    @Test
    fun `logUrl writes correct data to CSV`() {
        val tempFile = File.createTempFile("test", ".csv")
        val logger = FileUrlLogger(tempFile.absolutePath, FileExtension.CSV)

        val packageName = "com.example.app"
        val url = "http://example.com"
        val hookType = "Okhttp"

        logger.logUrl(packageName, url, hookType)

        val content = tempFile.readText()
        assert(content.contains("$packageName,$url"))

        tempFile.delete()
    }
}
