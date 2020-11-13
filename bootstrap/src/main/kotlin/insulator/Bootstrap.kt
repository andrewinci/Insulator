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
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.system.exitProcess

private val frame = JFrame("Bootstrap")
const val localPath = "/tmp/insulator-download/"
const val localConfigFile = "${localPath}insulator-update.xml"
val updatePath: Path = Path.of(System.getProperty("java.io.tmpdir") ?: "", "update.zip")

// https://github.com/andrea-vinci/Insulator/releases/latest/download/insulator-update.xml
const val configPath = "https://github.com/andrea-vinci/Insulator/releases/download/0.1.75/insulator-update.xml"

fun main(args: Array<String>) {
    URL(configPath).runCatching { openConnection().getInputStream() }
        .fold({ Result.success(it) }, { error ->
            tryLoadLocalConfig()?.let { Result.success(it) } ?: Result.failure(error)
        })
        .mapCatching { stream -> saveConfig(stream) }
        .mapCatching { stream -> InputStreamReader(stream).use { Configuration.read(it) } }
        .mapCatching { config ->
            if (config.requiresUpdate()) {
                val result = config.update(UpdateOptions.archive(updatePath).updateHandler(InsulatorUpdateHandler()))
                if (result.exception == null) Archive.read(updatePath).install()
                else throw result.exception
            }
            config
        }
        .mapCatching { it.launch() }
        .fold({}, {
            val errorMessage = when (it) {
                is UnknownHostException -> Triple("Unable to check for updates. Check your internet connection and retry", "Download error", JOptionPane.WARNING_MESSAGE)
                is SocketTimeoutException -> Triple("Unable to complete the download. Check your internet connection and retry", "Timeout error", JOptionPane.WARNING_MESSAGE)
                is FileNotFoundException -> Triple("Unable to find the remote configuration file. Please, contact the developer.", "Download error", JOptionPane.ERROR_MESSAGE)
                else -> Triple("Unexpected error: ${it}. Please, contact the developer.", "Unexpected error", JOptionPane.ERROR_MESSAGE)
            }
            JOptionPane.showMessageDialog(frame, errorMessage.first, errorMessage.second, errorMessage.third)
            exitProcess(-1)
        })
}

fun saveConfig(stream: InputStream) : InputStream {
    if (!File(localPath).exists()) File(localPath).mkdirs()
    Files.copy(
        stream,
        Paths.get(localConfigFile),
        StandardCopyOption.REPLACE_EXISTING)
    return FileInputStream(localConfigFile)
}

fun tryLoadLocalConfig() =
    with(File(localConfigFile)) {
        if (exists()) inputStream()
        else null
    }

class InsulatorUpdateHandler : UpdateHandler {
    private val view = BootstrapView(frame)
    override fun updateDownloadProgress(frac: Float) = view.updateDownloadProgress(frac)
}