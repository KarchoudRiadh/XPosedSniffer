package rk.app.datasniffingapp

import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.junit.Test
import org.mockito.Mockito.*
import rk.app.datasniffingapp.application.hooks.HookStrategy
import rk.app.datasniffingapp.domain.logger.UrlLogger
import rk.app.datasniffingapp.usecases.HookNetworkCallsUseCase

class HookNetworkCallsUseCaseTest {

    @Test
    fun `execute calls setupHooks on all strategies`() {
        val strategy1 = mock(HookStrategy::class.java)
        val strategy2 = mock(HookStrategy::class.java)

        val logger = mock(UrlLogger::class.java)
        val lpparam = mock(XC_LoadPackage.LoadPackageParam::class.java)

        val useCase = HookNetworkCallsUseCase(listOf(strategy1, strategy2))
        useCase.execute(lpparam, logger)

        verify(strategy1).setupHooks(lpparam, logger)
        verify(strategy2).setupHooks(lpparam, logger)
    }
}
