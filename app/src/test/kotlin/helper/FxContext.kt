package helper

import insulator.kafka.model.Cluster
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.io.Closeable

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
