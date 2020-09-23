package insulator.lib.update

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.map
import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.nio.file.Paths
import java.util.Properties

class VersionChecker {
    private val configFileName = "Insulator.cfg"
    private val versionProperty = "app.version"
    private val baseWebUrl = "https://github.com/darka91/Insulator/releases/tag/"
    private val lastReleaseApiCall = "https://api.github.com/repos/darka91/Insulator/releases/latest"

    fun getCurrentVersion(): Either<Unit, String> {
        val jarPath = this::class.java.protectionDomain.codeSource.location.path
        val jarFolder = Paths.get(URLDecoder.decode(jarPath, "UTF-8")).parent.toAbsolutePath().toString()
        val configPath = Paths.get(jarFolder, configFileName).toAbsolutePath().toString()
        return if (File(configPath).exists())
            Properties().also { it.load(FileInputStream(configPath)) }.getProperty(versionProperty).right()
        else Unit.left()
    }

    fun getLatestVersion(): Either<Throwable, Release> =
        lastReleaseApiCall.httpGet().responseJson().third
            .map { it.obj().getString("tag_name") to it.obj().getJSONArray("assets") }
            .map { (tag, assets) -> tag to assets.mapIndexed { id, _ -> assets.getJSONObject(id).getString("browser_download_url") } }
            .map { (tag, assetUrls) ->
                Release(
                    version = tag,
                    webUrl = Paths.get(baseWebUrl, tag).toString(),
                    debianUrl = assetUrls.first { it.contains("debian") },
                    macUrl = assetUrls.first { it.contains("mac") },
                    winUrl = assetUrls.first { it.contains("win") }
                )
            }.fold({ it.right() }, { it.left() })

}

data class Release(val version: String,
                   val webUrl: String,
                   val debianUrl: String,
                   val macUrl: String,
                   val winUrl: String)