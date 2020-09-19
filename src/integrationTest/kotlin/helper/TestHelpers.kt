package helper

import org.testfx.api.FxToolkit
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

fun configureDi(vararg dependencyMap: Pair<KClass<*>, Any>) {
    if (FX.dicontainer != null) throw TestHelperError("DI already configured")
    FX.dicontainer = object : DIContainer {
        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        override fun <T : Any> getInstance(type: KClass<T>): T =
            dependencyMap.toMap()[type] as? T
                ?: throw IllegalArgumentException("Missing dependency in test DI ${type.qualifiedName}")
    }
}

fun cleanupDi() {
    FX.dicontainer = null
}

fun configureFXFramework() {
    val stage = FxToolkit.registerPrimaryStage()
    FX.setPrimaryStage(stage = stage)
}

fun cleanupFXFramework() {
    FxToolkit.cleanupStages()
    cleanupDi()
}

class TestHelperError(message: String) : Throwable(message)
