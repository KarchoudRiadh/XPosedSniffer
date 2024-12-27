package rk.app.datasniffingapp

import de.robv.android.xposed.XposedBridge
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import rk.app.datasniffingapp.data.FileExtension
import rk.app.datasniffingapp.data.FileUrlLogger
import java.io.File
import java.io.FileWriter
import kotlin.test.assertTrue

class FileUrlLoggerTest {

    private lateinit var csvFileUrlLogger: FileUrlLogger
    private val csvMockFilePath = "mock/path/to/logfile.csv"
    private val csvMockFileExtension = FileExtension.CSV

    private lateinit var txtFileUrlLogger: FileUrlLogger
    private val txtMockFilePath = "mock/path/to/logfile.txt"
    private val txtMockFileExtension = FileExtension.TXT

    @BeforeEach
    fun setUp() {
        // Initialize the FileUrlLogger instance before each test
        csvFileUrlLogger =
            FileUrlLogger(filePath = csvMockFilePath, fileExtension = csvMockFileExtension)
        txtFileUrlLogger =
            FileUrlLogger(filePath = txtMockFilePath, fileExtension = txtMockFileExtension)
    }

    @Test
    fun `test logUrl creates CSV file and writes URL correctly`() {
        // Mocking the file system
        val mockFile = mockk<File>(relaxed = true)
        val mockFileWriter = mockk<FileWriter>(relaxed = true)
        val mockParentDir = mockk<File>(relaxed = true)

        // Mocking static methods and behavior
        mockkStatic(File::class)
        every { File(csvMockFilePath).parentFile } returns mockParentDir
        every { mockParentDir.exists() } returns false
        every { mockParentDir.mkdirs() } returns true
        every { File(csvMockFilePath).exists() } returns false
        every { FileWriter(csvMockFilePath, true) } returns mockFileWriter

        // Mocking XposedBridge.log for verification
        mockkStatic(XposedBridge::class)
        every { XposedBridge.log(any<String>()) } just Runs

        // Act: Call logUrl method
        csvFileUrlLogger.logUrl("com.example.app", "http://example.com", "HttpURLConnection")

        // Assert: Check if mkdirs() was called and log was written correctly
        verify { mockParentDir.mkdirs() }
        verify { mockFileWriter.appendLine("AnyLine") }
        verify { XposedBridge.log("[FileUrlLogger] HttpURLConnection - URL : http://example.com => logged to ${mockFile.canonicalPath + csvMockFileExtension.separator}") }

        // Optionally, assert that the file's parent directory exists and file creation is attempted
        assertTrue(File(csvMockFilePath).exists(), "File should be created at $csvMockFilePath")
    }

    @Test
    fun `test logUrl creates TXT file and writes URL correctly`() {
        // Mocking the file system
        val mockFile = mockk<File>(relaxed = true)
        val mockFileWriter = mockk<FileWriter>(relaxed = true)
        val mockParentDir = mockk<File>(relaxed = true)

        // Mocking static methods and behavior
        mockkStatic(File::class)
        every { File(txtMockFilePath).parentFile } returns mockParentDir
        every { mockParentDir.exists() } returns false
        every { mockParentDir.mkdirs() } returns true
        every { File(txtMockFilePath).exists() } returns false
        every { FileWriter(txtMockFilePath, true) } returns mockFileWriter

        // Mocking XposedBridge.log for verification
        mockkStatic(XposedBridge::class)
        every { XposedBridge.log(any<String>()) } just Runs

        // Act: Call logUrl method
        txtFileUrlLogger.logUrl("com.example.app", "http://example.com", "HttpURLConnection")

        // Assert: Check if mkdirs() was called and log was written correctly
        verify { mockParentDir.mkdirs() }
        verify { mockFileWriter.appendLine("AnyLine") }
        verify { XposedBridge.log("[FileUrlLogger] HttpURLConnection - URL : http://example.com => logged to ${mockFile.canonicalPath + txtMockFileExtension.separator}") }

        // Optionally, assert that the file's parent directory exists and file creation is attempted
        assertTrue(File(txtMockFilePath).exists(), "File should be created at $csvMockFilePath")
    }

    @Test
    fun `test logUrl handles IOException when creating file or directory`() {
        // Mocking the file system to throw an IOException
        val mockParentDir = mockk<File>(relaxed = true)

        mockkStatic(File::class)
        every { File(csvMockFilePath).parentFile } returns mockParentDir
        every { mockParentDir.exists() } returns false
        every { mockParentDir.mkdirs() } returns false // Simulate failure in directory creation

        // Mock XposedBridge.log for error logging
        mockkStatic(XposedBridge::class)
        every { XposedBridge.log(any<String>()) } just Runs

        // Act: Call logUrl and simulate an error in creating directories
        csvFileUrlLogger.logUrl("com.example.app", "http://example.com", "HttpURLConnection")

        // Assert: Verify that XposedBridge logs the error
        verify { XposedBridge.log("[FileUrlLogger] Failed to create parent directories for: $mockParentDir") }
    }
}
