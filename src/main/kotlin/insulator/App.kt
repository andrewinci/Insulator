package insulator

import insulator.di.DIContainer
import insulator.ui.style.styles
import insulator.views.configurations.ListClusterView
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.* // ktlint-disable no-wildcard-imports

class Insulator : App(ListClusterView::class, *styles) {

    override fun createPrimaryScene(view: UIComponent): Scene {
        FX.primaryStage.setOnCloseRequest { stop() }
        return super.createPrimaryScene(view)
    }

    override fun stop() {
        Window.getWindows().map { it as? Stage }
            .filter { it != FX.primaryStage }
            .forEach { it?.close() }
        super.stop()
    }
}

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching { launch<Insulator>(args) }
}
