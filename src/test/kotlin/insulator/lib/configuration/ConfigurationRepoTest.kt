package insulator.lib.configuration

import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import insulator.lib.configuration.model.SaslConfiguration
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.configuration.model.SslConfiguration
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import kotlin.random.Random

class ConfigurationRepoTest : FunSpec({
    val json = Json {}
    beforeTest {
        // clean up previous tests
        File(".").walk()
            .filter { it.isFile && it.name.startsWith("insulator.test.") }
            .forEach { it.delete() }
    }

    test("getConfiguration the first time create the config file") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(json, testConfig)
        // act
        val res = sut.getConfiguration()
        // assert
        res shouldBeRight Configuration(clusters = emptyList())
        File(testConfig).exists() shouldBe true
    }

    test("getConfiguration of a corrupted file return left") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        File(testConfig).writeText("Wrong content")
        val sut = ConfigurationRepo(json, testConfig)
        // act
        val res = sut.getConfiguration()
        // assert
        res shouldBeLeft {}
    }

    test("delete a cluster from the configuration") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(json, testConfig)
        val testCluster = UUID.randomUUID()
        sut.store(Cluster(testCluster, "Test", ""))
        // act
        val res = sut.delete(Cluster(testCluster, "", ""))
        // assert
        res shouldBeRight Unit
        File(testConfig).readText().replace("\n", "").replace(" ", "") shouldBe "{\"clusters\":[]}"
    }

    test("delete a cluster never added") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(json, testConfig)
        sut.store(Cluster(UUID.randomUUID(), "Test", ""))
        val expectedConfig = File(testConfig).readText()
        // act
        val res = sut.delete(Cluster(UUID.randomUUID(), "", ""))
        // assert
        res shouldBeRight Unit
        File(testConfig).readText() shouldBe expectedConfig
    }

    test("store a new cluster") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(json, testConfig)
        val uuid = UUID.randomUUID()
        // act
        val res = sut.store(Cluster(uuid, "", ""))
        // assert
        res shouldBeRight Unit
        ConfigurationRepo(json, testConfig).getConfiguration() shouldBeRight
            Configuration(clusters = listOf(Cluster(uuid, "", "")))
    }

    test("store a new cluster 2") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(json, testConfig)
        val uuid = UUID.randomUUID()
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

    afterTest {
        // clean up previous tests
        File(".").walk()
            .filter { it.isFile && it.name.startsWith("insulator.test.") }
            .forEach { it.delete() }
    }
})
