package helper

import org.koin.core.context.stopKoin
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlin.reflect.KClass

fun getTestSandboxFolder(): Path = Paths.get("test-sandbox", UUID.randomUUID().toString()).also { it.toFile().mkdirs() }
fun deleteTestSandboxFolder() = Paths.get("test-sandbox").toFile().deleteRecursively()

fun configureDi(vararg dependencyMap: Pair<KClass<*>, Any>) {
    if (FX.dicontainer != null) throw TestHelperError("DI already configured")
    FX.dicontainer = object : DIContainer {
        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        override fun <T : Any> getInstance(type: KClass<T>): T =
            dependencyMap.toMap()[type] as? T
                ?: throw IllegalArgumentException("Missing dependency in test DI ${type.qualifiedName}")
    }
}

fun configureScopeDi(vararg dependency: Configurable) = dependency.forEach {
    FX.defaultScope.set(it as ScopedInstance)
}

fun cleanupDi() {
    FX.dicontainer = null
    FX.defaultScope.deregister()
    stopKoin()
}

fun configureFXFramework() {
    val stage = FxToolkit.registerPrimaryStage()
    FX.setPrimaryStage(stage = stage)
}

fun cleanupFXFramework() {
    FxToolkit.cleanupStages()
    cleanupDi()
}

fun waitFXThread() {
    WaitForAsyncUtils.waitForFxEvents()
}

class TestHelperError(message: String) : Throwable(message)
