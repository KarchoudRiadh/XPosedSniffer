package rk.app.datasniffingapp.data

/**
 * A logger that aggregates multiple `UrlLogger` implementations.
 *
 * The `CompositeUrlLogger` allows for logging intercepted URLs to multiple destinations
 * (e.g., files, databases, or other logging mechanisms) by delegating the logging operation
 * to a list of individual loggers.
 *
 * @property loggers A list of `UrlLogger` instances to which logging operations are delegated.
 */
class CompositeUrlLogger(private val loggers: List<UrlLogger>) : UrlLogger {
    /**
     * Logs the intercepted URL to all registered loggers.
     *
     * This method iterates over the list of loggers and invokes their `logUrl` method with the
     * provided package name, URL, and hook type.
     *
     * @param packageName The package name of the application generating the network request.
     * @param url The URL being accessed.
     * @param hookType The type of hook that intercepted the network request.
     */
    override fun logUrl(packageName: String, url: String, hookType: String) {
        loggers.forEach { logger ->
            logger.logUrl(packageName = packageName, url = url, hookType = hookType)
        }
    }
}