package helper

import insulator.lib.configuration.model.Cluster
import org.koin.core.context.stopKoin
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.io.Closeable
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlin.reflect.KClass

fun getTestSandboxFolder(): Path = Paths.get("test-sandbox", UUID.randomUUID().toString()).also { it.toFile().mkdirs() }
fun deleteTestSandboxFolder() = Paths.get("test-sandbox").toFile().deleteRecursively()

open class FxContext : Closeable {
    private val diCache = mutableMapOf<KClass<*>, Any>()
    val cluster = Cluster.empty()

    init {
        val stage = FxToolkit.registerPrimaryStage()
        addToDI(Cluster::class to cluster)
        FX.setPrimaryStage(stage = stage)
    }

    fun addToDI(vararg dependencyMap: Pair<KClass<*>, Any>) {
        diCache.putAll(dependencyMap)
        FX.dicontainer = object : DIContainer {
            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            override fun <T : Any> getInstance(type: KClass<T>): T =
                diCache[type] as? T
                    ?: throw IllegalArgumentException("Missing dependency in test DI ${type.qualifiedName}")
        }
    }

    fun configureFxDi(vararg dependency: Configurable) = dependency.forEach {
        FX.defaultScope.set(it as ScopedInstance)
    }

    fun waitFXThread() {
        WaitForAsyncUtils.waitForFxEvents()
    }

    override fun close() {
        waitFXThread() // wait any pending task
        FxToolkit.cleanupStages()
        FX.dicontainer = null
        FX.defaultScope.deregister()
        stopKoin()
    }
}
