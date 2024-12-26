package rk.app.datasniffingapp.application.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.data.UrlLogger

class OkHttpHookStrategy : HookStrategy {
    override fun setupHooks(loadPackageParam: XC_LoadPackage.LoadPackageParam, logger: UrlLogger) {
        try {
            // Hook OkHttp synchronous execute() method
            XposedHelpers.findAndHookMethod(
                "okhttp3.internal.connection.RealCall",
                loadPackageParam.classLoader,
                "execute",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        logOKHttp(loadPackageParam, logger, param)
                    }
                }
            )

            // Hook OkHttp asynchronous enqueue() method
            XposedHelpers.findAndHookMethod(
                "okhttp3.internal.connection.RealCall",
                loadPackageParam.classLoader,
                "enqueue",
                XposedHelpers.findClass(
                    "okhttp3.Callback",
                    loadPackageParam.classLoader
                ),
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        logOKHttp(loadPackageParam, logger, param)
                    }
                }
            )

            XposedBridge.log("[OkHttpHookStrategy] OkHttp hooks successfully set up.")
        } catch (e: Throwable) {
            XposedBridge.log("[OkHttpHookStrategy] Failed to hook OkHttp: ${e.message}")
        }
    }

    private fun logOKHttp(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        logger: UrlLogger,
        param: MethodHookParam
    ) {
        XposedHelpers.callMethod(
            XposedHelpers.callMethod(
                param.thisObject,
                "request"
            ), "url"
        ).toString().let { url ->
            XposedBridge.log("[OkHttpHookStrategy] OkHttp request enqueued. URL: $url")
            logger.logUrl(
                packageName = loadPackageParam.packageName,
                url = url,
                hookType = javaClass.name
            )
        }
    }
}
