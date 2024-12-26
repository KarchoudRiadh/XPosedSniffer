package rk.app.datasniffingapp.usecases

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.application.hooks.HookStrategy
import rk.app.datasniffingapp.domain.logger.UrlLogger

/**
 * Use case to hook into network calls in the target app.
 * @param strategies List of HookStrategy implementations to apply.
 */
class HookNetworkCallsUseCase(private val strategies: List<HookStrategy>) {
    /**
     * Executes the hook setup for all strategies.
     * @param loadPackageParam LoadPackage parameters provided by Xposed.
     * @param logger The URL logger to be used by the hooks.
     */
    fun execute(loadPackageParam: XC_LoadPackage.LoadPackageParam, logger: UrlLogger) {
        strategies.forEach { strategy ->
            strategy.setupHooks(loadPackageParam = loadPackageParam, logger = logger)
        }
    }
}
