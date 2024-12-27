package rk.app.datasniffingapp

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import rk.app.datasniffingapp.application.hooks.HttpURLConnectionHookStrategy
import rk.app.datasniffingapp.data.UrlLogger
import java.net.URL

class HttpURLConnectionHookStrategyTest {

    private lateinit var httpURLConnectionHookStrategy: HttpURLConnectionHookStrategy
    private val mockLoadPackageParam: XC_LoadPackage.LoadPackageParam = mock()
    private val mockUrlLogger: UrlLogger = mock()

    @BeforeEach
    fun setUp() {
        // Initialize the HttpURLConnectionHookStrategy
        httpURLConnectionHookStrategy = HttpURLConnectionHookStrategy()

        // Mock loadPackageParam properties
        `when`(mockLoadPackageParam.packageName).thenReturn("com.example.app")
        `when`(mockLoadPackageParam.classLoader).thenReturn(javaClass.classLoader)
    }

    @Test
    fun `test setupHooks success`() {
        // Given a mocked method hook
        val mockMethodHook = mock<XC_MethodHook.MethodHookParam>()

        // When findAndHookMethod is called
        Mockito.doNothing().`when`(XposedHelpers::class.java).findAndHookMethod(
            "java.net.URL",
            mockLoadPackageParam.classLoader,
            "openConnection",
            Mockito.any(XC_MethodHook::class.java)
        )

        // Call setupHooks
        httpURLConnectionHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)

        // Verify that findAndHookMethod was called
        verify(XposedHelpers).findAndHookMethod(
            "java.net.URL",
            mockLoadPackageParam.classLoader,
            "openConnection",
            Mockito.any(XC_MethodHook::class.java)
        )

        // Verify the logUrl method of the logger is called
        verify(mockUrlLogger).logUrl(
            packageName = "com.example.app",
            url = Mockito.any(),
            hookType = "rk.app.datasniffingapp.application.hooks.HttpURLConnectionHookStrategy"
        )

        // Verify that the XposedBridge log was called for successful setup
        verify(XposedBridge).log("[HttpURLConnectionHookStrategy] HttpURLConnection hooks successfully set up.")
    }

    @Test
    fun `test setupHooks failure`() {
        // Simulate an error in the hook setup (e.g., method not found)
        Mockito.doThrow(Throwable("Method not found")).`when`(XposedHelpers::class.java)
            .findAndHookMethod(
                "java.net.URL",
                mockLoadPackageParam.classLoader,
                "openConnection",
                Mockito.any(XC_MethodHook::class.java)
            )

        // Call setupHooks
        httpURLConnectionHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)

        // Verify that XposedBridge logs the error message
        verify(XposedBridge).log("[HttpURLConnectionHookStrategy] Failed to hook HttpURLConnection: Method not found")
    }

    @Test
    fun `test afterHookedMethod logs URL`() {
        // Simulate an invocation of afterHookedMethod with mock params
        val mockMethodHookParam = mock<XC_MethodHook.MethodHookParam>()
        `when`(mockMethodHookParam.thisObject).thenReturn(URL("http://example.com"))

        // Call the afterHookedMethod directly
        httpURLConnectionHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)

        // Simulate invoking the afterHookedMethod
        httpURLConnectionHookStrategy.afterHookedMethod(mockMethodHookParam)

        // Verify that the logUrl method is called with the correct URL
        verify(mockUrlLogger).logUrl(
            packageName = "com.example.app",
            url = "http://example.com",
            hookType = "rk.app.datasniffingapp.application.hooks.HttpURLConnectionHookStrategy"
        )

        // Verify that XposedBridge logs the URL
        verify(XposedBridge).log("[HttpURLConnectionHookStrategy] HttpURLConnection opened. URL: http://example.com")
    }
}
