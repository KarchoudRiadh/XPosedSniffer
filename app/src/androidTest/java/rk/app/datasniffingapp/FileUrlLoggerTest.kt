package rk.app.datasniffingapp

import org.junit.Test
import rk.app.datasniffingapp.data.logger.FileExtension
import rk.app.datasniffingapp.data.logger.FileUrlLogger
import java.io.File

class FileUrlLoggerTest {

    @Test
    fun `logUrl writes correct data to CSV`() {
        val tempFile = File.createTempFile("test", ".csv")
        val logger = FileUrlLogger(tempFile.absolutePath, FileExtension.CSV)

        val packageName = "com.example.app"
        val url = "http://example.com"
        val hookType = "Volley"

        logger.logUrl(packageName, url, hookType)

        val content = tempFile.readText()
        assert(content.contains("$packageName,$url"))

        tempFile.delete()
    }

    @Test
    fun `logUrl writes correct data to TXT`() {
        val tempFile = File.createTempFile("test", ".txt")
        val logger = FileUrlLogger(tempFile.absolutePath, FileExtension.TXT)

        val packageName = "com.example.app"
        val url = "http://example.com"
        val hookType = "Volley"

        logger.logUrl(packageName, url, hookType)

        val content = tempFile.readText()
        assert(content.contains("$packageName,$url"))

        tempFile.delete()
    }
}
