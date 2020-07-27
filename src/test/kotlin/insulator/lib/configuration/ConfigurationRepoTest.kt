package insulator.lib.configuration

import com.google.gson.Gson
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.util.UUID
import kotlin.random.Random

class ConfigurationRepoTest : FunSpec({

    beforeTest {
        // clean up previous tests
        File(".").walk()
            .filter { it.isFile && it.name.startsWith("insulator.test.") }
            .forEach { it.delete() }
    }

    test("getConfiguration the first time create the config file") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(Gson(), testConfig)
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
        val sut = ConfigurationRepo(Gson(), testConfig)
        // act
        val res = sut.getConfiguration()
        // assert
        res shouldBeLeft {}
    }

    test("delete a cluster from the configuration") {
        // arrange
        val testConfig = "./insulator.test.${Random.nextLong()}"
        val sut = ConfigurationRepo(Gson(), testConfig)
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
        val sut = ConfigurationRepo(Gson(), testConfig)
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
        val sut = ConfigurationRepo(Gson(), testConfig)
        val uuid = UUID.randomUUID()
        // act
        val res = sut.store(Cluster(uuid, "", ""))
        // assert
        res shouldBeRight Unit
        ConfigurationRepo(Gson(), testConfig).getConfiguration() shouldBeRight
            Configuration(clusters = listOf(Cluster(uuid, "", "")))
    }

    afterTest {
        // clean up previous tests
        File(".").walk()
            .filter { it.isFile && it.name.startsWith("insulator.test.") }
            .forEach { it.delete() }
    }
})
