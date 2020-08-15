package insulator.viewmodel.configurations

import arrow.core.left
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.ConfigurationRepoException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

class ListClusterViewModelTest : FunSpec({

    test("Show an error if unable to retrieve the configuration") {
        // arrange
        val sut = ListClusterViewModel()
        // act
        val clusters = sut.clustersProperty
        // assert
        clusters.size shouldBe 0
        sut.error.value!!.message shouldBe "Example error"
    }

    beforeTest {
        FX.dicontainer = object : DIContainer {
            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            override fun <T : Any> getInstance(type: KClass<T>): T = when (type.java) {
                ConfigurationRepo::class.java -> mockk<ConfigurationRepo> {
                    every { addNewClusterCallback(any()) } just runs
                    every { getConfiguration() } returns ConfigurationRepoException("Example error", Throwable()).left()
                }
                else -> throw IllegalArgumentException("Missing dependency")
            } as T
        }
    }
})
