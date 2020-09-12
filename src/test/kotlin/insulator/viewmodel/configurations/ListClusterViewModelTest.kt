package insulator.viewmodel.configurations

import arrow.core.left
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.ConfigurationRepoException
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
        // act
        val clusters = sut.clustersProperty
        // assert
        clusters.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    beforeTest {
        configureFXFramework()
        configureDi(
            ConfigurationRepo::class to mockk<ConfigurationRepo> {
                every { addNewClusterCallback(any()) } just runs
                every { getConfiguration() } returns ConfigurationRepoException(errorMessage, Throwable()).left()
            }
        )
    }
})
