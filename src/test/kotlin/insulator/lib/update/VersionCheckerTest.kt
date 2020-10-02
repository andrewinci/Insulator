package insulator.lib.update

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import insulator.di.GITHUB_REPO
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import java.io.Closeable
import java.nio.file.Paths

class VersionCheckerTest : FunSpec({

    test("happy path getCurrentVersion with a new release") {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker(it.mockJarPath)
            it.mockCurrentAppVersion("0.0.8")
            val sampleMessage = Paths.get("src", "test", "resources", "githubResponseSample.json").toFile().readText()
            it.mockHttpResponse(200, sampleMessage)

            // act
            val version = sut.getCurrentVersion()
            // assert
            version shouldBeRight Version("0.0.8", it.mockRelease)
        }
    }

    test("happy path getCurrentVersion having the latest release") {
        VersionCheckerTestFixture().use {
            // arrange
            val mockVersion = "0.0.9"
            val sut = VersionChecker(it.mockJarPath)
            it.mockCurrentAppVersion(mockVersion)
            val sampleMessage = Paths.get("src", "test", "resources", "githubResponseSample.json").toFile().readText()
            it.mockHttpResponse(200, sampleMessage)

            // act
            val version = sut.getCurrentVersion()

            // assert
            version shouldBeRight Version(mockVersion, null)
        }
    }

    test("happy path getAppVersion") {
        VersionCheckerTestFixture().use {
            // arrange
            val mockVersion = "1.2.3"
            val sut = VersionChecker(it.mockJarPath)
            it.mockCurrentAppVersion(mockVersion)
            // act
            val latestVersion = sut.getAppVersion()
            // assert
            latestVersion shouldBeRight mockVersion
        }
    }

    test("happy path getLatestVersion") {
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

    test("get latest version with a server error return left") {
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

    test("get latest version with an invalid json return left") {
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

    test("get latest version with an invalid response return left") {
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

    test("get app version with a wrong formatted config file return left") {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker(it.mockJarPath)
            with(Paths.get("src", "test", "resources", "Insulator.cfg").toFile()) {
                createNewFile()
                writeText("invalid string in the config file")
            }

            // act
            val latestVersion = sut.getAppVersion()

            // assert
            latestVersion shouldBeLeft {}
        }
    }

    test("get app version with a missing config file return left") {
        VersionCheckerTestFixture().use {
            // arrange
            val sut = VersionChecker(it.mockJarPath)
            with(Paths.get("src", "test", "resources", "Insulator.cfg").toFile()) {
                delete()
            }

            // act
            val latestVersion = sut.getAppVersion()

            // assert
            latestVersion shouldBeLeft {}
        }
    }
})

class VersionCheckerTestFixture : Closeable {
    fun mockHttpResponse(statusCode: Int, jsonResponse: String) {
        FuelManager.instance.client = mockk<Client> {
            every { executeRequest(any()).statusCode } returns statusCode
            every { executeRequest(any()).data } returns jsonResponse.toByteArray()
        }
    }

    private val remoteTag = "0.0.9"
    private val releasesPath = "$GITHUB_REPO/releases"
    private val downloadUrls = listOf("debian", "mac", "win").map { "$releasesPath/download/$remoteTag/insulator-$it.zip" }

    val mockRelease = Release(remoteTag, "$releasesPath/tag/$remoteTag", downloadUrls[0], downloadUrls[1], downloadUrls[2])
    val mockJarPath = Paths.get("src", "test", "resources", "mock.jar").toFile().absolutePath

    fun mockCurrentAppVersion(version: String) {
        with(Paths.get("src", "test", "resources", "Insulator.cfg").toFile()) {
            createNewFile()
            writeText("app.version=$version")
        }
    }

    override fun close() {
        FuelManager.instance.reset()
    }
}
