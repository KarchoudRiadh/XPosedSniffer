package rk.app.datasniffingapp

import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import rk.app.datasniffingapp.data.CompositeUrlLogger
import rk.app.datasniffingapp.data.FileExtension
import rk.app.datasniffingapp.data.SQLiteUrlLogger
import rk.app.datasniffingapp.data.FileUrlLogger
import rk.app.datasniffingapp.utils.Constants
import rk.app.datasniffingapp.domain.HookNetworkCallsUseCase
import de.robv.android.xposed.XposedBridge
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import rk.app.datasniffingapp.application.SnifferXposedModule
import kotlin.enums.EnumEntries

class SnifferXposedModuleTest {

    private lateinit var snifferXposedModule: SnifferXposedModule
    private val mockLoadPackageParam: XC_LoadPackage.LoadPackageParam = mock()
    private val mockCompositeLogger: CompositeUrlLogger = mock()
    private val mockHookNetworkCallsUseCase: HookNetworkCallsUseCase = mock()

    @BeforeEach
    fun setUp() {
        // Mock necessary objects
        Mockito.`when`(mockLoadPackageParam.packageName).thenReturn("com.example.target")
        Mockito.`when`(mockLoadPackageParam.classLoader).thenReturn(javaClass.classLoader)

        // Initialize SnifferXposedModule with mocked dependencies
        snifferXposedModule = SnifferXposedModule()
    }

    @Test
    fun `test handleLoadPackage with correct package`() {
        // Given a valid target package
        Mockito.`when`(mockLoadPackageParam.packageName).thenReturn(Constants.TARGET_PACKAGE)

        // Mock the compositeLogger behavior
        Mockito.`when`(mockCompositeLogger.logUrl(Mockito.any(), Mockito.any(), Mockito.any())).thenAnswer { }

        // Call the handleLoadPackage method
        snifferXposedModule.handleLoadPackage(mockLoadPackageParam)

        // Verify that the appropriate methods were called
        verify(mockCompositeLogger).logUrl(Mockito.any(), Mockito.any(), Mockito.any())
        verify(mockHookNetworkCallsUseCase).execute(mockLoadPackageParam, mockCompositeLogger)
        verify(mockLoadPackageParam).packageName
        verify(mockLoadPackageParam).classLoader
        XposedBridge.log("[XposedModule] Loaded target app: ${mockLoadPackageParam.packageName}")
    }

    @Test
    fun `test handleLoadPackage with incorrect package`() {
        // Given a non-target package
        Mockito.`when`(mockLoadPackageParam.packageName).thenReturn("com.some.other.package")

        // Call the handleLoadPackage method
        snifferXposedModule.handleLoadPackage(mockLoadPackageParam)

        // Verify that the network calls are not hooked
        verify(mockHookNetworkCallsUseCase, Mockito.never()).execute(Mockito.any(), Mockito.any())
    }

    @Test
    fun `test compositeLogger construction with correct file extensions`() {
        // Mock FileExtension.entries and file extensions
        val mockFileExtensionCSV = mock<FileExtension>()
        val mockFileExtensionDB = mock<FileExtension>()

        val fileExtensions = listOf(mockFileExtensionCSV, mockFileExtensionDB)
        Mockito.`when`(FileExtension.entries).thenReturn(fileExtensions as EnumEntries<FileExtension>?)

        // Mock the necessary file path operations
        val mockSQLiteUrlLogger = mock<SQLiteUrlLogger>()
        val mockFileUrlLogger = mock<FileUrlLogger>()

        // Call handleLoadPackage
        snifferXposedModule.handleLoadPackage(mockLoadPackageParam)

        // Verify that compositeLogger is initialized with correct loggers
        verify(mockSQLiteUrlLogger).logUrl(Mockito.any(), Mockito.any(), Mockito.any())
        verify(mockFileUrlLogger).logUrl(Mockito.any(), Mockito.any(), Mockito.any())
    }
}
