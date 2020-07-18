package insulator

import insulator.lib.koin.DIContainer
import insulator.views.configurations.ListClusterView
import javafx.stage.Stage
import tornadofx.*

class Insulator : App(ListClusterView::class, Styles::class) {
}

fun main(args: Array<String>) {
    FX.dicontainer = DIContainer()
    runCatching { launch<Insulator>(args) }
}