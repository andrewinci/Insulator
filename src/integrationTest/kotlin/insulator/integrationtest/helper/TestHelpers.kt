package insulator.integrationtest.helper

import javafx.stage.Stage
import org.testfx.api.FxToolkit
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

fun configureIntegrationDi(vararg dependencyMap: Pair<KClass<*>, Any>) {
    if (FX.dicontainer != null) throw TestHelperError("DI already configured")
    FX.dicontainer = object : DIContainer {
        val main = insulator.di.DIContainer()

        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        override fun <T : Any> getInstance(type: KClass<T>): T =
            dependencyMap.toMap()[type] as? T ?: main.getInstance(type)
    }
}

fun waitPrimaryStage(limit: Int = 20): Stage {
    repeat(limit) {
        val primaryStage = FX.getPrimaryStage()
        if (primaryStage?.isShowing == true && primaryStage.isFocused) {
            Thread.sleep(1000)
            return primaryStage
        }
        Thread.sleep(1000)
    }
    throw TestHelperError("Timeout waiting for primary stage to show up")
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
