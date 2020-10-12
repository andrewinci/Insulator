package insulator.viewmodel.configurations

import arrow.core.left
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import helper.waitFXThread
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.ConfigurationRepoException
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import insulator.lib.helpers.runOnFXThread
import insulator.views.common.StringScope
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearStaticMockk
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import tornadofx.FX
import java.util.UUID

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
            every { clusters } returns listOf(Cluster.empty(), Cluster.empty(), Cluster.empty())
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

    test("open edit cluster view happy path") {
        // arrange
        val sut = ListClusterViewModel()
        val cluster = Cluster.empty()
        val scope = StringScope("Cluster-${cluster.guid}")
        // act
        sut.runOnFXThread { openEditClusterWindow(cluster) }
        waitFXThread()
        // assert
        FX.getComponents(scope).size shouldBe 2 // View and viewModel
    }

    test("open edit cluster view to add a new cluster happy path") {
        // arrange
        val sut = ListClusterViewModel()
        val uuid = UUID.randomUUID()
        // act
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid
        sut.runOnFXThread { openEditClusterWindow() }
        waitFXThread()
        // assert
        FX.getComponents(StringScope("Cluster-$uuid")).size shouldBe 2 // View and viewModel
        clearStaticMockk()
    }

    test("openMainWindow happy path") {
        // arrange
        val sut = ListClusterViewModel()
        val cluster = Cluster.empty()
        var stringScope: StringScope? = null
        // act
        sut.openMainWindow(cluster = cluster) { stringScope = it }
        // assert
        stringScope shouldNotBe null
        stringScope?.value shouldBe "Cluster-${cluster.guid}"
    }

    beforeTest { configureFXFramework() }

    afterTest { cleanupFXFramework() }
})
