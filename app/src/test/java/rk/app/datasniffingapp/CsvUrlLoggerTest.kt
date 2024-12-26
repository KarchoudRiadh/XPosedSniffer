package rk.app.datasniffingapp

import org.junit.Test
import rk.app.datasniffingapp.data.logger.CsvUrlLogger
import java.io.File

class CsvUrlLoggerTest {

    @Test
    fun `logUrl writes correct data to CSV`() {
        val tempFile = File.createTempFile("test", ".csv")
        val logger = CsvUrlLogger(tempFile.absolutePath)

        val packageName = "com.example.app"
        val url = "http://example.com"

        logger.logUrl(packageName, url)

        val content = tempFile.readText()
        assert(content.contains("$packageName,$url"))

        tempFile.delete()
    }
}
