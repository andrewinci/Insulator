package insulator

import org.update4j.Archive
import org.update4j.Configuration
import org.update4j.UpdateOptions
import org.update4j.service.UpdateHandler
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import kotlin.system.exitProcess

private val view = BootstrapViewManager(JFrame("Bootstrap").apply { defaultCloseOperation = DISPOSE_ON_CLOSE })

fun main(args: Array<String>) {
    tryLoadLocalConfig()
        .let { (left, right) -> left?.let { Result.failure(it) } ?: Result.success(right!!) }
        .mapCatching { config ->
            if (config.requiresUpdate()) {
                val result =
                    config.update(UpdateOptions.archive(TMP_UPDATE_PATH).updateHandler(InsulatorUpdateHandler()))
                result.exception?.let { throw it } ?: Archive.read(TMP_UPDATE_PATH).install()
            }
            config
        }
        .mapCatching { it.launch() }
        .fold({ Unit }, { handleErrors(it) })
}

private fun handleErrors(exception: Throwable) {
    val errorMessage = when (exception) {
        is UnknownHostException -> Triple(
            "Unable to check for updates. Check your internet connection and retry",
            "Download error",
            JOptionPane.WARNING_MESSAGE
        )
        is SocketTimeoutException -> Triple(
            "Unable to complete the download. Check your internet connection and retry",
            "Timeout error",
            JOptionPane.WARNING_MESSAGE
        )
        is FileNotFoundException -> Triple(
            "Unable to find the remote configuration file.",
            "Download error",
            JOptionPane.ERROR_MESSAGE
        )
        else -> Triple("Unexpected error: $exception.", "Unexpected error", JOptionPane.ERROR_MESSAGE)
    }
    view.showMessageDialog(errorMessage.first, errorMessage.second, errorMessage.third)
    exitProcess(-1)
}

fun saveConfig(stream: InputStream): InputStream {
    if (!File(LOCAL_CONFIG_PATH).exists()) File(LOCAL_CONFIG_PATH).mkdirs()
    Files.copy(
        stream,
        Paths.get(LOCAL_CONFIG_FILE_PATH),
        StandardCopyOption.REPLACE_EXISTING
    )
    return FileInputStream(LOCAL_CONFIG_FILE_PATH)
}

fun tryLoadLocalConfig(): Pair<Throwable?, Configuration?> =
    URL(getUpdateConfigURL()).runCatching { openConnection().getInputStream() }
        .fold(
            { Result.success(it) },
            { error ->
                with(File(LOCAL_CONFIG_FILE_PATH)) {
                    if (exists()) Result.success(inputStream())
                    else Result.failure(error)
                }
            }
        )
        .mapCatching { stream -> saveConfig(stream) }
        .mapCatching { stream -> InputStreamReader(stream).use { Configuration.read(it) } }
        .fold({ Pair(null, it) }, { Pair(it, null) })

fun getUpdateConfigURL() =
    if (File(DEV_FILE_PATH).exists())
        "https://github.com/andrewinci/Insulator/releases/download/${File(DEV_FILE_PATH).readText().trim()}/insulator-update.xml"
    else LATEST_RELEASE_URL

class InsulatorUpdateHandler : UpdateHandler {
    override fun startDownloads() = view.showUpdateView()
    override fun updateDownloadProgress(frac: Float) = view.updateDownloadProgress(frac)
    override fun doneDownloads() = view.closeUpdateView()
}
