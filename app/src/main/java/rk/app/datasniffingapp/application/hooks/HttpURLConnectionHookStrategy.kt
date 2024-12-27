package rk.app.datasniffingapp.application.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.data.UrlLogger

class HttpURLConnectionHookStrategy : HookStrategy {
    override fun setupHooks(loadPackageParam: XC_LoadPackage.LoadPackageParam, logger: UrlLogger) {
        try {
            XposedHelpers.findAndHookMethod(
                "java.net.URL",
                loadPackageParam.classLoader,
                "openConnection",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val url = param.thisObject.toString()
                        XposedBridge.log("[HttpURLConnectionHookStrategy] HttpURLConnection opened. URL: $url")
                        logger.logUrl(
                            packageName = loadPackageParam.packageName,
                            url = url,
                            hookType = javaClass.name
                        )
                    }
                }
            )

            XposedBridge.log("[HttpURLConnectionHookStrategy] HttpURLConnection hooks successfully set up.")
        } catch (e: Throwable) {
            XposedBridge.log("[HttpURLConnectionHookStrategy] Failed to hook HttpURLConnection: ${e.message}")
        }
    }
}