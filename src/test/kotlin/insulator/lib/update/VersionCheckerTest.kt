package insulator.lib.update

import com.github.kittinunf.fuel.core.FuelManager
import helper.getTestSandboxFolder
import insulator.di.GITHUB_REPO
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import java.io.Closeable
import java.nio.file.Path
import java.nio.file.Paths

class VersionCheckerTest : StringSpec({

    "happy path getCurrentVersion with a new release" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker(it.mockInsulatorConfigPath.toString())
            it.mockCurrentAppVersion("0.0.8")
            val sampleMessage = Paths.get("src", "test", "resources", "githubResponseSample.json").toFile().readText()
            it.mockHttpResponse(200, sampleMessage)
            // act
            val version = sut.getCurrentVersion()
            // assert
            version shouldBeRight Version("0.0.8", it.mockRelease)
        }
    }

    "happy path getCurrentVersion having the latest release" {
        VersionCheckerTestFixture().use {
            // arrange
            val mockVersion = "0.0.9"
            val sut = VersionChecker(it.mockInsulatorConfigPath.toString())
            it.mockCurrentAppVersion(mockVersion)
            val sampleMessage = Paths.get("src", "test", "resources", "githubResponseSample.json").toFile().readText()
            it.mockHttpResponse(200, sampleMessage)
            // act
            val version = sut.getCurrentVersion()
            // assert
            version shouldBeRight Version(mockVersion, null)
        }
    }

    "happy path getAppVersion" {
        VersionCheckerTestFixture().use {
            // arrange
            val mockVersion = "1.2.3"
            val sut = VersionChecker(it.mockInsulatorConfigPath.toString())
            it.mockCurrentAppVersion(mockVersion)
            // act
            val latestVersion = sut.getAppVersion()
            // assert
            latestVersion shouldBeRight mockVersion
        }
    }

    "happy path getLatestVersion" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker()
            val sampleMessage = Paths.get("src", "test", "resources", "githubResponseSample.json").toFile().readText()
            it.mockHttpResponse(200, sampleMessage)
            // act
            val latestVersion = sut.getLatestVersion()
            // assert
            latestVersion shouldBeRight it.mockRelease
        }
    }

    "get latest version with a server error return left" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker()
            it.mockHttpResponse(500, """Internal server error""")
            // act
            val latestVersion = sut.getLatestVersion()
            // assert
            latestVersion shouldBeLeft {}
        }
    }

    "get latest version with an invalid json return left" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker()
            it.mockHttpResponse(200, """Invalid json here""")
            // act
            val latestVersion = sut.getLatestVersion()
            // assert
            latestVersion shouldBeLeft {}
        }
    }

    "get latest version with an invalid response return left" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker()
            it.mockHttpResponse(200, """{"error": "Json response changed"}""")
            // act
            val latestVersion = sut.getLatestVersion()
            // assert
            latestVersion shouldBeLeft {}
        }
    }

    "get app version with a wrong formatted config file return left" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker(it.mockInsulatorConfigPath.toString())
            with(it.mockInsulatorConfigPath.toFile()) {
                createNewFile()
                writeText("invalid string in the config file")
            }
            // act
            val latestVersion = sut.getAppVersion()
            // assert
            latestVersion shouldBeLeft {}
        }
    }

    "get app version with a missing config file return left" {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker("random_path")
            // act
            val latestVersion = sut.getAppVersion()
            // assert
            latestVersion shouldBeLeft {}
        }
    }
})

class VersionCheckerTestFixture : Closeable {
    fun mockHttpResponse(statusCode: Int, jsonResponse: String) {
        FuelManager.instance.client = mockk {
            coEvery { awaitRequest(any()).data } returns jsonResponse.toByteArray()
            coEvery { awaitRequest(any()).statusCode } returns statusCode
        }
    }

    private val remoteTag = "0.0.9"
    private val releasesPath = "$GITHUB_REPO/releases"
    private val downloadUrls = listOf("debian", "mac", "win").map { "$releasesPath/download/$remoteTag/insulator-$it.zip" }
    private val testDir = getTestSandboxFolder()

    val mockRelease = Release(remoteTag, "$releasesPath/tag/$remoteTag", downloadUrls[0], downloadUrls[1], downloadUrls[2])
    val mockInsulatorConfigPath: Path = Paths.get(testDir.toString(), "Insulator.cfg")

    fun mockCurrentAppVersion(version: String) {
        with(mockInsulatorConfigPath.toFile()) {
            testDir.toFile().mkdirs()
            createNewFile()
            writeText("app.version=$version")
        }
    }

    override fun close() {
        FuelManager.instance.reset()
        with(mockInsulatorConfigPath.toFile()) {
            testDir.toFile().mkdirs()
            delete()
        }
    }
}
