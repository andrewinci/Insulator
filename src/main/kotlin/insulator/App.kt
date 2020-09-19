package insulator

import insulator.di.DIContainer
import insulator.styles.Controls
import insulator.styles.Root
import insulator.styles.Theme
import insulator.styles.Titles
import insulator.views.configurations.ListClusterView
import javafx.application.Platform
import javafx.scene.Scene
import tornadofx.App
import tornadofx.FX
import tornadofx.UIComponent
import tornadofx.launch
import kotlin.system.exitProcess

class Insulator : App(ListClusterView::class, Theme::class, Titles::class, Root::class, Controls::class) {
    override fun createPrimaryScene(view: UIComponent): Scene {
        FX.primaryStage.setOnCloseRequest { stop() }
        return super.createPrimaryScene(view)
    }
    override fun stop() {
        super.stop()
        Platform.exit()
        exitProcess(0)
    }
}

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching { launch<Insulator>(args) }
}
