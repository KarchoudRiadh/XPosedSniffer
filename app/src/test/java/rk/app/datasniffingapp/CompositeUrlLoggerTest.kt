package rk.app.datasniffingapp

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import rk.app.datasniffingapp.data.CompositeUrlLogger
import rk.app.datasniffingapp.data.UrlLogger

class CompositeUrlLoggerTest {

    @Test
    fun `logUrl calls all underlying loggers`() {
        val logger1 = mock(UrlLogger::class.java)
        val logger2 = mock(UrlLogger::class.java)

        val compositeLogger = CompositeUrlLogger(listOf(logger1, logger2))
        val packageName = "com.example.app"
        val url = "http://example.com"
        val hookType = "Volley"

        compositeLogger.logUrl(packageName, url, hookType)

        verify(logger1).logUrl(packageName, url, hookType)
        verify(logger2).logUrl(packageName, url, hookType)
    }
}
