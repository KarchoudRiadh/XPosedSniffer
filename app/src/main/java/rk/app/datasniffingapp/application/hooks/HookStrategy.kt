package rk.app.datasniffingapp.application.hooks

import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.data.UrlLogger

/**
 * Represents a strategy for hooking into specific network request mechanisms
 * to intercept and log URLs.
 *
 * This interface defines a contract for implementing different hooking strategies
 * for various networking libraries (e.g., OkHttp, HttpURLConnection, Volley).
 * Each implementation should specify how to set up hooks for the desired library
 * or framework.
 */
interface HookStrategy {

    /**
     * Sets up hooks to intercept and log network requests within the target application.
     *
     * @param loadPackageParam An instance of `XC_LoadPackage.LoadPackageParam`
     *                         that provides information about the loaded package,
     *                         such as the target app's package name and class loader.
     * @param logger           An instance of `UrlLogger` used to log the intercepted URLs.
     */
    fun setupHooks(loadPackageParam: XC_LoadPackage.LoadPackageParam, logger: UrlLogger)
}
