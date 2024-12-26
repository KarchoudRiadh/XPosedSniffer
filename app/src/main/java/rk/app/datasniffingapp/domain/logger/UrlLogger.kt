package rk.app.datasniffingapp.domain.logger

/**
 * Interface for logging URLs.
 */
interface UrlLogger {
    /**
     * Logs a URL with an associated package name.
     * @param packageName The app's package name.
     * @param url The URL to be logged.
     */
    fun logUrl(packageName: String, url: String, hookType: String)
}
