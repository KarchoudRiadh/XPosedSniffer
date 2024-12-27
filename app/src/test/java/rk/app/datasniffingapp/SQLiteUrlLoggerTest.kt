package rk.app.datasniffingapp

import android.database.sqlite.SQLiteDatabase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import rk.app.datasniffingapp.data.SQLiteUrlLogger
import java.io.File

class SQLiteUrlLoggerTest {

    private lateinit var sqliteUrlLogger: SQLiteUrlLogger
    private val mockDatabasePath = "mock/database/path"
    private val mockDatabase: SQLiteDatabase = mock()
    private val mockFile: File = mock()

    @BeforeEach
    fun setUp() {
        // Mock the static methods used in the SQLiteUrlLogger class
        Mockito.`when`(SQLiteDatabase.openOrCreateDatabase(mockDatabasePath, null)).thenReturn(mockDatabase)
        Mockito.`when`(File(mockDatabasePath).parentFile).thenReturn(mockFile)
        Mockito.`when`(mockFile.exists()).thenReturn(true)

        // Create an instance of SQLiteUrlLogger
        sqliteUrlLogger = SQLiteUrlLogger(mockDatabasePath)
    }

    @Test
    fun testLogUrlSuccessfully() {
        // Given a valid package name, URL, and hook type
        val packageName = "com.example.app"
        val url = "https://example.com"
        val hookType = "HTTP"

        // When the logUrl method is called
        sqliteUrlLogger.logUrl(packageName, url, hookType)

        // Then the database interaction should happen
        verify(mockDatabase).compileStatement("INSERT INTO UrlLogs (packageName, url, hookType) VALUES (?, ?, ?)")
    }

    @Test
    fun testDatabaseInitialization() {
        // Simulate the creation of the parent directory and database
        val file = File(mockDatabasePath)
        Mockito.`when`(file.exists()).thenReturn(true)
        sqliteUrlLogger = SQLiteUrlLogger(mockDatabasePath)

        // Verify that the database is initialized correctly
        verify(mockDatabase).execSQL("CREATE TABLE IF NOT EXISTS UrlLogs (packageName TEXT, url TEXT, hookType TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)")
    }

    @Test
    fun testLogUrlWithExceptionHandling() {
        // Simulate an exception when the database is being accessed
        Mockito.`when`(SQLiteDatabase.openOrCreateDatabase(mockDatabasePath, null)).thenThrow(RuntimeException("Database error"))

        // Given a valid package name, URL, and hook type
        val packageName = "com.example.app"
        val url = "https://example.com"
        val hookType = "HTTP"

        // When the logUrl method is called, it should catch and log the exception without crashing
        sqliteUrlLogger.logUrl(packageName, url, hookType)

    }

    @Test
    fun testCreateParentDirectoriesIfNeeded() {
        // Simulate that the parent directory does not exist
        Mockito.`when`(mockFile.exists()).thenReturn(false)
        Mockito.`when`(mockFile.mkdirs()).thenReturn(true)

        // Call the method that should trigger parent directory creation
        sqliteUrlLogger = SQLiteUrlLogger(mockDatabasePath)

        // Verify that the mkdirs method was called
        verify(mockFile).mkdirs()
    }

    @Test
    fun testLogUrlWithFileCreation() {
        // Test that a file is created if it doesn't exist
        val file = File(mockDatabasePath)
        Mockito.`when`(file.exists()).thenReturn(false)
        Mockito.`when`(file.createNewFile()).thenReturn(true)

        // Given a valid package name, URL, and hook type
        val packageName = "com.example.app"
        val url = "https://example.com"
        val hookType = "HTTP"

        // When the logUrl method is called
        sqliteUrlLogger.logUrl(packageName, url, hookType)

        // Verify that the file was created
        verify(file).createNewFile()
    }
}
