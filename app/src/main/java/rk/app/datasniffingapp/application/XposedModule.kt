package rk.app.datasniffingapp.application

import android.os.Environment
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.application.hooks.HookStrategy
import rk.app.datasniffingapp.application.hooks.HttpURLConnectionHookStrategy
import rk.app.datasniffingapp.application.hooks.OkHttpHookStrategy
import rk.app.datasniffingapp.application.hooks.VolleyHookStrategy
import rk.app.datasniffingapp.data.logger.CompositeUrlLogger
import rk.app.datasniffingapp.data.logger.FileExtension
import rk.app.datasniffingapp.data.logger.FileUrlLogger
import rk.app.datasniffingapp.data.logger.SQLiteUrlLogger
import rk.app.datasniffingapp.usecases.HookNetworkCallsUseCase
import rk.app.datasniffingapp.utils.Constants.FILE_NAME
import rk.app.datasniffingapp.utils.Constants.TARGET_PACKAGE

class SnifferXposedModule : IXposedHookLoadPackage {

    private val hookStrategies: List<HookStrategy> = listOf(
        OkHttpHookStrategy(),
        HttpURLConnectionHookStrategy(),
        VolleyHookStrategy()
    )

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