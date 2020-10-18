package insulator.lib.update

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.github.kittinunf.fuel.json.jsonDeserializer
import com.vdurmont.semver4j.Semver
import insulator.di.CONFIG_FILE_NAME
import insulator.di.GITHUB_REPO
import insulator.di.LATEST_RELEASE_API_ENDPOINT
import insulator.di.VERSION_PROPERTY
import insulator.lib.helpers.runCatchingE
import insulator.lib.helpers.toEitherOfList
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URLDecoder
import java.nio.file.Paths
import java.util.Properties

data class Version(val version: String, val latestRelease: Release?)
data class Release(val version: String, val webUrl: String, val debianUrl: String, val macUrl: String, val winUrl: String)

class VersionChecker(private val customJarPath: String? = null) {

    private val jarPath: String
        get() = customJarPath ?: Paths.get(this::class.java.protectionDomain.codeSource.location.toURI()).toString()

    suspend fun getCurrentVersion(): Either<Throwable, Version> = either {
        val appVersion = getAppVersion().fold({ "0.0.0" }, { it })
        val latestVersion = !getLatestVersion()
        val isANewVersionAvailable = Semver(appVersion).isLowerThan(latestVersion.version)
        Version(appVersion, if (isANewVersionAvailable) latestVersion else null)
    }

    fun getAppVersion(): Either<Throwable, String> {
        val jarFolder = Paths.get(URLDecoder.decode(jarPath, "UTF-8")).parent?.toAbsolutePath()?.toString()
            ?: return FileNotFoundException().left()
        val configPath = Paths.get(jarFolder, CONFIG_FILE_NAME).toAbsolutePath().toString()
        return if (File(configPath).exists())
            Properties()
                .also { it.load(FileInputStream(configPath)) }
                .getProperty(VERSION_PROPERTY)?.right()
                ?: Error(VERSION_PROPERTY).left()
        else FileNotFoundException().left()
    }

    suspend fun getLatestVersion(): Either<Throwable, Release> = either {
        val jsonObject = !Fuel.get(LATEST_RELEASE_API_ENDPOINT).awaitObjectResult(jsonDeserializer())
            .fold({ it.right() }, { it.left() })
            .flatMap { it.runCatchingE { obj() } }
        val tag = !jsonObject.runCatchingE { getString("tag_name") }
        val assets = !jsonObject.runCatchingE { getJSONArray("assets") }
        val assetUrls = !assets
            .mapIndexed { id, _ -> assets.runCatchingE { getJSONObject(id).getString("browser_download_url") } }
            .toEitherOfList()
        Release(
            version = tag,
            webUrl = "$GITHUB_REPO/releases/tag/$tag",
            macUrl = assetUrls.first { it.contains("mac") },
            winUrl = assetUrls.first { it.contains("win") },
            debianUrl = assetUrls.first { it.contains("debian") },
        )
    }
}
