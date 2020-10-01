package insulator.viewmodel.configurations

import arrow.core.left
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.ConfigurationRepoException
import insulator.lib.configuration.model.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs

class ListClusterViewModelTest : FunSpec({

    val errorMessage = "Example error"

    test("Show an error if unable to retrieve the configuration") {
        // arrange
        val sut = ListClusterViewModel()
        configureDi(
            ConfigurationRepo::class to mockk<ConfigurationRepo> {
                every { addNewClusterCallback(any()) } just runs
                every { getConfiguration() } returns ConfigurationRepoException(errorMessage, Throwable()).left()
            }
        )
        // act
        val clusters = sut.clustersProperty
        // assert
        clusters.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    test("Update the list of cluster when a new one is added") {
        // arrange
        val sut = ListClusterViewModel()
        val newMockConfiguration = mockk<Configuration> {
            every { clusters } returns listOf(mockk(), mockk(), mockk())
        }
        lateinit var callback: (Configuration) -> Unit
        configureDi(
            ConfigurationRepo::class to mockk<ConfigurationRepo> {
                every { addNewClusterCallback(any()) } answers { callback = firstArg() }
                every { getConfiguration() } returns ConfigurationRepoException(errorMessage, Throwable()).left()
            }
        )
        val cluster = sut.clustersProperty
        cluster.size shouldBe 0
        // act
        callback(newMockConfiguration)
        // assert
        cluster.size shouldBe 3
    }

    beforeTest { configureFXFramework() }

    afterTest { cleanupFXFramework() }
})
