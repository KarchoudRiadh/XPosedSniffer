package rk.app.datasniffingapp

import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.mockk.mockk
import org.junit.Test
import org.mockito.Mockito.verify
import rk.app.datasniffingapp.application.hooks.HookStrategy
import rk.app.datasniffingapp.data.UrlLogger
import rk.app.datasniffingapp.domain.HookNetworkCallsUseCase

class HookNetworkCallsUseCaseTest {

    @Test
    fun `execute calls setupHooks for all strategies`() {
        val strategy1 = mockk<HookStrategy>(relaxed = true)
        val strategy2 = mockk<HookStrategy>(relaxed = true)
        val logger = mockk<UrlLogger>(relaxed = true)
        val loadPackageParam = mockk<XC_LoadPackage.LoadPackageParam>()

        val useCase = HookNetworkCallsUseCase(listOf(strategy1, strategy2))
        useCase.execute(loadPackageParam, logger)

        verify { strategy1.setupHooks(loadPackageParam, logger) }
        verify { strategy2.setupHooks(loadPackageParam, logger) }
    }
}