package rk.app.datasniffingapp.application

import android.os.Environment
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.application.hooks.HookStrategy
import rk.app.datasniffingapp.application.hooks.HttpURLConnectionHookStrategy
import rk.app.datasniffingapp.application.hooks.OkHttpHookStrategy
import rk.app.datasniffingapp.application.hooks.VolleyHookStrategy
import rk.app.datasniffingapp.data.CompositeUrlLogger
import rk.app.datasniffingapp.data.FileExtension
import rk.app.datasniffingapp.data.FileUrlLogger
import rk.app.datasniffingapp.data.SQLiteUrlLogger
import rk.app.datasniffingapp.domain.HookNetworkCallsUseCase
import rk.app.datasniffingapp.utils.Constants.FILE_NAME
import rk.app.datasniffingapp.utils.Constants.TARGET_PACKAGE

/**
 * Xposed module implementation for sniffing network traffic in a target application.
 *
 * This module hooks into the specified target package and applies various network request interception
 * strategies (e.g., OkHttp, HttpURLConnection, and Volley). Intercepted URLs are logged using
 * multiple storage mechanisms such as files and SQLite databases.
 */
class SnifferXposedModule : IXposedHookLoadPackage {

    private val hookStrategies: List<HookStrategy> = listOf(
        OkHttpHookStrategy(),
        HttpURLConnectionHookStrategy(),
        VolleyHookStrategy()
    )

    /**
     * Handles the loading of the target package.
     *
     * If the loaded package matches the specified target package, this method sets up
     * network request interception and logging using the defined hook strategies.
     *
     * @param loadPackageParam Contains information about the loaded package, including its class loader.
     */
    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        if (loadPackageParam.packageName != TARGET_PACKAGE) {
            return
        }

        val compositeLogger = CompositeUrlLogger(
            FileExtension.entries.map { fileExtension ->
                if (fileExtension.name == FileExtension.DB.name) {
                    SQLiteUrlLogger(databasePath = "${Environment.getDataDirectory().path}/data/${loadPackageParam.packageName}/${FILE_NAME}.${fileExtension.name.lowercase()}")
                } else {
                    FileUrlLogger(
                        filePath = "${Environment.getDataDirectory().path}/data/${loadPackageParam.packageName}/${FILE_NAME}.${fileExtension.name.lowercase()}",
                        fileExtension = fileExtension
                    )
                }
            })

        XposedBridge.log("[XposedModule] Loaded target app: ${loadPackageParam.packageName}")
        HookNetworkCallsUseCase(hookStrategies).execute(loadPackageParam, compositeLogger)
    }
}