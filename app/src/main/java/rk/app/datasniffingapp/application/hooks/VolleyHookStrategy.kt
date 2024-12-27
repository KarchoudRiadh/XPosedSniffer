package rk.app.datasniffingapp.application.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rk.app.datasniffingapp.data.UrlLogger

class VolleyHookStrategy : HookStrategy {
    override fun setupHooks(loadPackageParam: XC_LoadPackage.LoadPackageParam, logger: UrlLogger) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.volley.RequestQueue",
                loadPackageParam.classLoader,
                "add",
                "com.android.volley.Request",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val request = param.args[0]
                        request.javaClass.getMethod("getUrl").invoke(request)?.toString()
                            ?.let { url ->
                                XposedBridge.log("[VolleyHookStrategy] Volley request added. URL: $url")
                                logger.logUrl(
                                    packageName = loadPackageParam.packageName,
                                    url = url,
                                    hookType = javaClass.name
                                )
                            }
                    }
                }
            )

            XposedBridge.log("[VolleyHookStrategy] Volley hooks successfully set up.")
        } catch (e: Throwable) {
            XposedBridge.log("[VolleyHookStrategy] Failed to hook Volley: ${e.message}")
        }
    }
}