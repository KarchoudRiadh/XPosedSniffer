package rk.app.datasniffingapp


import com.android.volley.RequestQueue
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.Request
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import rk.app.datasniffingapp.application.hooks.VolleyHookStrategy
import rk.app.datasniffingapp.data.UrlLogger

class VolleyHookStrategyTest {

    private lateinit var volleyHookStrategy: VolleyHookStrategy
    private val mockLoadPackageParam: XC_LoadPackage.LoadPackageParam = mock()
    private val mockUrlLogger: UrlLogger = mock()
    private val mockRequestQueue: RequestQueue = mock()
    private val mockRequest: Request<*> = mock()

    @BeforeEach
    fun setUp() {
        // Initialize the VolleyHookStrategy
        volleyHookStrategy = VolleyHookStrategy()

        // Mock loadPackageParam properties
        whenever(mockLoadPackageParam.packageName).thenReturn("com.example.app")
        whenever(mockLoadPackageParam.classLoader).thenReturn(javaClass.classLoader)
    }

    @Test
    fun `test setupHooks success`() {
        // Given that findAndHookMethod is called on the right class and method
        Mockito.doNothing().`when`(XposedHelpers::class.java).findAndHookMethod(
            "com.android.volley.RequestQueue",
            mockLoadPackageParam.classLoader,
            "add",
            Request::class.java,
            Mockito.any(XC_MethodHook::class.java)
        )

        // Call setupHooks
        volleyHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)

        // Verify that findAndHookMethod was called for the "add" method in RequestQueue
        verify(XposedHelpers).findAndHookMethod(
            "com.android.volley.RequestQueue",
            mockLoadPackageParam.classLoader,
            "add",
            Request::class.java,
            Mockito.any(XC_MethodHook::class.java)
        )

        // Verify that XposedBridge logs the success message
        verify(XposedBridge).log("[VolleyHookStrategy] Volley hooks successfully set up.")
    }

    @Test
    fun `test beforeHookedMethod logs URL`() {
        // Prepare a mock Volley Request and simulate the "getUrl" method
        val url = "http://example.com"
        whenever(mockRequest.javaClass.getMethod("getUrl")).thenReturn(mockMethod("getUrl"))
        whenever(mockRequest.javaClass.getMethod("getUrl").invoke(mockRequest)).thenReturn(url)

        // Simulate calling beforeHookedMethod
        val mockMethodHookParam = mock<XC_MethodHook.MethodHookParam>()
        whenever(mockMethodHookParam.args).thenReturn(arrayOf(mockRequest))

        // Call setupHooks first to set up the hook
        volleyHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)

        // Invoke the beforeHookedMethod to simulate the behavior
        volleyHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)
        volleyHookStrategy.beforeHookedMethod(mockMethodHookParam)

        // Verify that the logUrl method is called with the correct URL
        verify(mockUrlLogger).logUrl(
            packageName = "com.example.app",
            url = url,
            hookType = "rk.app.datasniffingapp.application.hooks.VolleyHookStrategy"
        )

        // Verify that XposedBridge logs the correct URL
        verify(XposedBridge).log("[VolleyHookStrategy] Volley request added. URL: $url")
    }

    @Test
    fun `test setupHooks failure`() {
        // Simulate an error in the hook setup (e.g., method not found)
        Mockito.doThrow(Throwable("Method not found")).`when`(XposedHelpers::class.java).findAndHookMethod(
            "com.android.volley.RequestQueue",
            mockLoadPackageParam.classLoader,
            "add",
            Request::class.java,
            Mockito.any(XC_MethodHook::class.java)
        )

        // Call setupHooks
        volleyHookStrategy.setupHooks(mockLoadPackageParam, mockUrlLogger)

        // Verify that XposedBridge logs the error message
        verify(XposedBridge).log("[VolleyHookStrategy] Failed to hook Volley: Method not found")
    }

    // Helper method to mock reflection-based method invocation
    private fun mockMethod(methodName: String): java.lang.reflect.Method {
        return Request::class.java.getMethod(methodName)
    }
}
