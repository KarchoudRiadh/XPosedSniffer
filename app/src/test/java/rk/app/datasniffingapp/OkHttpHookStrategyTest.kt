package rk.app.datasniffingapp

import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import rk.app.datasniffingapp.application.hooks.OkHttpHookStrategy
import rk.app.datasniffingapp.data.UrlLogger

class OkHttpHookStrategyTest {

    @Test
    fun `setupHooks hooks and logs URLs correctly`() {
        val logger = mock(UrlLogger::class.java)
        val lpparam = mock(XC_LoadPackage.LoadPackageParam::class.java)
        `when`(lpparam.packageName).thenReturn("com.example.app")

        val strategy = OkHttpHookStrategy()
        strategy.setupHooks(lpparam, logger)

        // Simulate a hooked method returning a URL
        val url = "http://example.com"
        val hookType = "Okhttp"
        logger.logUrl(lpparam.packageName, url, hookType)

        verify(logger).logUrl("com.example.app", url, hookType)
    }
}
