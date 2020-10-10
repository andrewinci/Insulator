package insulator

import insulator.di.DIContainer
import insulator.views.configurations.ListClusterView
import insulator.views.style.AppBarStyle
import insulator.views.style.ButtonStyle
import insulator.views.style.CheckBoxStyle
import insulator.views.style.ComboBoxStyle
import insulator.views.style.DialogPaneStyle
import insulator.views.style.ListViewStyle
import insulator.views.style.MainViewStyle
import insulator.views.style.Root
import insulator.views.style.ScrollPaneStyle
import insulator.views.style.TableViewStyle
import insulator.views.style.TextStyle
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.* // ktlint-disable no-wildcard-imports

class Insulator : App(
    ListClusterView::class,
    Root::class,
    AppBarStyle::class,
    ButtonStyle::class,
    CheckBoxStyle::class,
    ComboBoxStyle::class,
    ListViewStyle::class,
    TableViewStyle::class,
    TextStyle::class,
    DialogPaneStyle::class,
    ScrollPaneStyle::class,
    MainViewStyle::class,
) {

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
