package rk.app.datasniffingapp

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import rk.app.datasniffingapp.data.CompositeUrlLogger
import rk.app.datasniffingapp.data.UrlLogger

class UrlLoggerInterfaceTest {
    @Test
    fun `composite logger calls all loggers`() {
        val mockLogger1 = mockk<UrlLogger>(relaxed = true)
        val mockLogger2 = mockk<UrlLogger>(relaxed = true)
        val compositeLogger = CompositeUrlLogger(listOf(mockLogger1, mockLogger2))

        compositeLogger.logUrl("com.example", "http://example.com", "TestHook")

        verify { mockLogger1.logUrl("com.example", "http://example.com", "TestHook") }
        verify { mockLogger2.logUrl("com.example", "http://example.com", "TestHook") }
    }
}