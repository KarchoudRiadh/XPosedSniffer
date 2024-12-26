package rk.app.datasniffingapp

import org.junit.Test
import org.mockito.Mockito.*
import rk.app.datasniffingapp.data.logger.CompositeUrlLogger
import rk.app.datasniffingapp.domain.logger.UrlLogger

class CompositeUrlLoggerTest {

    @Test
    fun `logUrl calls all underlying loggers`() {
        val logger1 = mock(UrlLogger::class.java)
        val logger2 = mock(UrlLogger::class.java)

        val compositeLogger = CompositeUrlLogger(listOf(logger1, logger2))
        val packageName = "com.example.app"
        val url = "http://example.com"

        compositeLogger.logUrl(packageName, url)

        verify(logger1).logUrl(packageName, url)
        verify(logger2).logUrl(packageName, url)
    }
}
