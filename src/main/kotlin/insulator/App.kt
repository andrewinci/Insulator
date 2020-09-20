package insulator

import insulator.di.DIContainer
import insulator.styles.Controls
import insulator.styles.Root
import insulator.styles.Theme
import insulator.styles.Titles
import insulator.views.configurations.ListClusterView
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.App
import tornadofx.FX
import tornadofx.UIComponent
import tornadofx.launch

class Insulator : App(ListClusterView::class, Theme::class, Titles::class, Root::class, Controls::class) {

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
