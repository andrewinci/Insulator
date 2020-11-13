package insulator

import org.update4j.Archive
import org.update4j.Configuration
import org.update4j.UpdateOptions
import org.update4j.service.DefaultUpdateHandler
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Path

fun main(args: Array<String>) {
    // https://github.com/andrea-vinci/Insulator/releases/latest/download/insulator-update.xml
    val tempDir = System.getProperty("java.io.tmpdir")
    val configPath = "https://github.com/andrea-vinci/Insulator/releases/download/0.1.75/insulator-update.xml"
    val url = URL(configPath)
    InputStreamReader(url.openConnection().getInputStream()).use {
        val config = Configuration.read(it)
        val updatePath = Path.of(tempDir, "update.zip")
        if (config.requiresUpdate()) {
            val result = config.update(UpdateOptions.archive(updatePath).updateHandler(DefaultUpdateHandler()))
            if (result.exception == null) Archive.read(updatePath).install()
            // else something went wrong
        }
        config.launch()
    }
}