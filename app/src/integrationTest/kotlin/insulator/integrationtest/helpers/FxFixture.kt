package insulator.integrationtest.helpers

import insulator.Insulator
import insulator.configuration.ConfigurationRepo
import insulator.kafka.model.Cluster
import insulator.test.helper.deleteTestSandboxFolder
import insulator.test.helper.getTestSandboxFolder
import kotlinx.coroutines.delay
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils
import tornadofx.FX
import java.io.Closeable

class FxFixture() : Closeable {
    private val currentHomeFolder = getTestSandboxFolder().toAbsolutePath()

    init {
        val stage = FxToolkit.registerPrimaryStage()
        FX.setPrimaryStage(stage = stage)
        // override home user to avoid messing up with current user configs
        System.setProperty("user.home", currentHomeFolder.toString())
    }

    suspend fun startAppWithClusters(vararg clusters: Cluster) {
        storeConfiguration(*clusters)
        FxToolkit.setupApplication(Insulator::class.java)
        // wait a bit, CI may be slow
        waitFXThread()
        delay(5_000)
    }

    private suspend fun storeConfiguration(vararg cluster: Cluster) =
        ConfigurationRepo("$currentHomeFolder/.insulator.config").let { repo ->
            cluster.forEach { repo.store(it) }
        }

    fun waitFXThread() {
        WaitForAsyncUtils.waitForFxEvents()
    }

    override fun close() {
        waitFXThread() // wait any pending task
        FxToolkit.cleanupStages()
        deleteTestSandboxFolder()
    }
}
