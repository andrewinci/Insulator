package helper

import insulator.lib.configuration.model.Cluster
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.io.Closeable
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

fun getTestSandboxFolder(): Path = Paths.get("test-sandbox", UUID.randomUUID().toString()).also { it.toFile().mkdirs() }
fun deleteTestSandboxFolder() = Paths.get("test-sandbox").toFile().deleteRecursively()

open class FxContext : Closeable {
    val cluster = Cluster.empty()

    init {
        val stage = FxToolkit.registerPrimaryStage()
        FX.setPrimaryStage(stage = stage)
    }

    fun waitFXThread() {
        WaitForAsyncUtils.waitForFxEvents()
    }

    override fun close() {
        waitFXThread() // wait any pending task
        FxToolkit.cleanupStages()
    }
}
