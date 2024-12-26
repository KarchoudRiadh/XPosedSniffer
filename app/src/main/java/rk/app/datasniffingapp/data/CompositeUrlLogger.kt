package rk.app.datasniffingapp.data

// CompositeUrlLogger to aggregate multiple loggers
class CompositeUrlLogger(private val loggers: List<UrlLogger>) : UrlLogger {
    override fun logUrl(packageName: String, url: String, hookType: String) {
        loggers.forEach { logger ->
            logger.logUrl(packageName = packageName, url = url, hookType = hookType)
        }
    }
}