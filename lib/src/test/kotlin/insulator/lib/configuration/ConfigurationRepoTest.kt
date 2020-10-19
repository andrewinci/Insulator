package insulator.lib.configuration

import helper.getTestSandboxFolder
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import insulator.lib.configuration.model.SaslConfiguration
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.configuration.model.SslConfiguration
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths
import java.util.UUID

class ConfigurationRepoTest : FreeSpec({
    val json = Json {}
    fun mockConfigPath() = Paths.get(getTestSandboxFolder().toString(), ".insulator.test").toString()

    "getConfiguration invokes the callback on change" - {
        // arrange
        val testConfig = mockConfigPath()
        val sut = ConfigurationRepo(json, testConfig)
        val testCluster = Cluster.empty()
        var callbackCalled: Configuration? = null

        "call the callback on store" {
            // act
            sut.addNewClusterCallback { callbackCalled = it }
            sut.store(testCluster)
            // assert
            callbackCalled!!.clusters shouldContain testCluster
        }

        "call the callback on deltete" {
            // act
            sut.addNewClusterCallback { callbackCalled = it }
            sut.delete(testCluster)
            // assert
            callbackCalled!!.clusters.isEmpty() shouldBe true
        }
    }

    "getConfiguration return left with invalid files" - {
        // arrange
        val testConfig = "http://something"
        val sut = ConfigurationRepo(json, testConfig)

        "left on retrieve configurations" {
            // act
            val res = sut.getConfiguration()
            // assert
            res shouldBeLeft { it.shouldBeInstanceOf<ConfigurationRepoException>() }
        }

        "left on store configurations" {
            // act
            val res = sut.store(Cluster.empty())
            // assert
            res shouldBeLeft { it.shouldBeInstanceOf<ConfigurationRepoException>() }
        }
    }

    "getConfiguration the first time create the config file" {
        // arrange
        val testConfig = mockConfigPath()
        val sut = ConfigurationRepo(json, testConfig)
        // act
        val res = sut.getConfiguration()
        // assert
        res shouldBeRight Configuration(clusters = emptyList())
        File(testConfig).exists() shouldBe true
    }

    "getConfiguration of a corrupted file return left" {
        // arrange
        val testConfig = mockConfigPath()
        File(testConfig).writeText("Wrong content")
        val sut = ConfigurationRepo(json, testConfig)
        // act
        val res = sut.getConfiguration()
        // assert
        res shouldBeLeft {}
    }

    "delete a cluster" - {
        // arrange
        val testConfig = mockConfigPath()
        val sut = ConfigurationRepo(json, testConfig)

        "delete a cluster from the configuration" {
            val testCluster = UUID.randomUUID()
            sut.store(Cluster(testCluster, "Test", ""))
            // act
            val res = sut.delete(Cluster(testCluster, "", ""))
            // assert
            res shouldBeRight Unit
            File(testConfig).readText().replace("\n", "").replace(" ", "") shouldBe "{\"clusters\":[]}"
        }

        "delete a cluster never added" {
            // arrange
            sut.store(Cluster(UUID.randomUUID(), "Test", ""))
            val expectedConfig = File(testConfig).readText()
            // act
            val res = sut.delete(Cluster(UUID.randomUUID(), "", ""))
            // assert
            res shouldBeRight Unit
            File(testConfig).readText() shouldBe expectedConfig
        }
    }

    "store a new cluster" - {
        // arrange
        val testConfig = mockConfigPath()
        val sut = ConfigurationRepo(json, testConfig)
        val uuid = UUID.randomUUID()

        "minimal cluster" {
            // act
            val res = sut.store(Cluster(uuid, "", ""))
            // assert
            res shouldBeRight Unit
            ConfigurationRepo(json, testConfig).getConfiguration() shouldBeRight
                Configuration(clusters = listOf(Cluster(uuid, "", "")))
        }
        "store a cluster with all configs" {
            // act
            val res = sut.store(
                Cluster(
                    uuid,
                    "",
                    "",
                    true,
                    SslConfiguration("", "", "", ""),
                    true,
                    SaslConfiguration("", ""),
                    SchemaRegistryConfiguration("", "", "")
                )
            )
            // assert
            res shouldBeRight Unit
            ConfigurationRepo(json, testConfig).getConfiguration() shouldBeRight {}
        }
    }
})
